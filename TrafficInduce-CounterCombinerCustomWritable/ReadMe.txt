New API (0.20)
---------------------


The Use Case
------------

It’s 11pm on a Thursday, and while Los Angeles is known for its atrocious traffic, you can usually count on being safe five from heavy traffic hours after rush hour.
But when you merge onto the I-10 going west, it’s bumper to bumper for miles!  What’s going on?

It has to be the Clippers game. With tens of thousands of cars leaving from the Staples Center after a home-team basketball game, of course it’s going to be bad.
But what about for a Lakers game?  How bad does it for those?  And what about holidays and during political events? It would be great if you could enter a time
and determine how far traffic deviated from average for every road in the city.

CalTrans’ Performance and Measurement System (PeMS) provides detailed traffic data from sensors placed on freeways across the state, with updates coming in every
30 seconds. The Los Angeles area alone contains over 4,000 sensor stations.  While this is frankly a boatload of data, MapReduce allows you to leverage a cluster
to process it in a reasonable amount of time.

In this post, we’ll write a MapReduce program that computes the averages, and next time, we’ll write a program that uses this information to build an index of
this data, so that a program may query it easily to display data from the relevant time.

The Program
--------------------

For our first MapReduce job, we would like to find the average traffic for each sensor station at each time of the week. While the data is available every 30 seconds,
we don’t need such fine granularity, so we will use the five-minute summaries that PeMS also publishes. Thus, with 4,370 stations, we will be calculating 
4,370 * (60 / 5) * 24 * 7 = 8,809,920 averages.

Each of our input data files contains the measurements for all the stations over a month. Each line contains a station ID, a time, some information about the station,
and the measurements taken from that station during that time interval.

Here are some example lines. The fields that are useful to us are the first, which tells the time; the second, which tells the station ID; and the 10th, which gives a
normalized vehicle at that station at that time.

01/01/2012 00:00:00,312346,3,80,W,ML,.491,354,33,1128,.0209,71.5,0,0,0,0,0,0,260,.012,73.9,432,.0273,69.2,436,.0234,72.3,,,,,,,,,,,,,,,

01/01/2012 00:00:00,312347,3,80,W,OR,,236,50,91,,,,,,,,,91,,,,,,,,,,,,,,,,,,,,,,,

01/01/2012 00:00:00,312382,3,99,N,ML,.357,0,0,629,.0155,67,0,0,0,0,0,0,330,.0159,69.9,299,.015,63.9,,,,,,,,,,,,,,,,,,

01/01/2012 00:00:00,312383,3,99,N,OR,,0,0,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,

01/01/2012 00:00:00,312386,3,99,N,ML,.42,0,0,1336,.0352,67.1,0,0,0,0,0,0,439,.0309,70.4,494,.039,67.4,403,.0357,63.4,,,,,,,,,,,,,,,

The mappers will parse the input lines and emit a key/value pair for each line, where the key is an ID that combines the station ID with the time of the week, and 
the value is the number of cars that passed over that sensor during that time. Each call to the reduce function receives a station/time of week and the vehicle count
values over all the weeks, and computes their average.

Combiners
---------------
An interesting inefficiency to note is that if a single mapper processes measurements over multiple weeks, it will end up with multiple outputs going to the same reducer.
As these outputs are going to be averaged by the reducer anyway, we would be able to save I/O by computing partial averages before we have the complete data. To do this,
we would need to maintain a count of how many data points are in each partial average, so that we can weight our final average by that count.  For example, we could
collapse a set of map outputs like 5, 6, 9, 10 into (avg=7.5, count=4). As each map output is written to disk on the mapper, sent over the network, and then possibly written
to disk on the reducer, reducing the number of outputs in this way can save a fair amount of I/O.

MapReduce provides us with a way to do exactly this in the form of combiner functions. The framework calls the combiner function in between the map and reduce phase, with 
the combiner’s outputs sent to the reducer instead of the map outputs that it’s called on. The framework may choose to call a combiner function zero or more times – generally
it is called before map outputs are persisted to disk, both on the map and reduce side.

Custom Writables
------------------
MapReduce key and value classes implement Hadoop’s Writable interface so that they can be serialized to and from binary. While Hadoop provides a set of classes that implement
Writable to serialize primitive types, the tuples we use in your pseudo-code don’t map efficiently onto any of them. For our keys, we can concatenate the station ID with the
time of week to represent them as strings and use the Text type.  However, as our value tuple is composed of primitive types, a float and an integer, it would be nice not to
have to convert them to and from strings each time you want to use them. We can accomplish this by implementing a Writable for them.

We deploy our Writable by including it in our job jar. To instantiate our Writable, the framework will call its no-argument constructor, and then fill it in by calling its
readFields method. Note that if we wanted to use a custom class as a key, it would need to implement WritableComparable so that it would be able to be sorted.

Counters
-----------
In the real world, data is messy. Traffic sensor data, for example, contains records with missing fields all the time, as sensors in the wild are bound to malfunction at times.
Running our MapReduce job, it is often useful to count up and collect metrics on the side about what our job is doing. For a program on a single computer, we might just do 
this by adding in a count variable, incrementing it whenever our event of interest occurs, and printing it out at the end, but when our code is running in a distributed fashion,
aggregating these counts gets hairy very quickly. 

Luckily, Hadoop provides a mechanism to handle this for us, using Counters. MapReduce contains a number of built-in counters that you have probably seen in the output on
completion of a MapReduce job.

Map-Reduce Framework

       Map input records=10

       Map output records=7

       Map output bytes=175

       [and many more]

This information is also available in the web UI, both per-job and per-task. To use our own counter, we can simply add a line like

context.getCounter("Averager Counters", "Missing vehicle flows").increment(1);

to the point in the code where the mapper comes across a record with a missing count. Then, when our job completes, we will see our count along with the built-in counters:

Averager Counters
   Missing vehicle flows=2329

It’s often convenient to wrap your entire map or reduce function in a try/catch, and increment a counter in the catch block, using the exception class’s name as the counter’s name
for a profile of what kind of errors come up.
