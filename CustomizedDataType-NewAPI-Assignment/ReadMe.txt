In BigramCount we need to count the frequency of the occurrence of two words together in the text.
So we are going to define a custom class (TextPair) that is going to hold the two words together.

Writable interface has two methods which must be implemented by XXX class.
write(dataOutput out) - It is used to serialize the fields of the object to "out"
readFields(DataInput in) - It is used to deserialize the fields of the object from "in"

CompareTo(WritableCompareable o) - It is inherited from Comparable interface and it allows Hadoop to sort the keys in the sort and shuffle phase

And just as you would for any value object you write in Java, you should override the hashCode(), equals(), 
and toString() methods from java.lang.Object. The hashCode() method is used by the HashPartitioner
(the default partitioner in MapReduce) to choose a reduce partition, so you should make sure that you 
write a good hash function that mixes well to ensure reduce partitions are of a similar size.


The Mapper just as the mapper of the wordCount example, takes the combination to two adjacent words and emits the TextPair and a value of ’1′.

Hadoop takes all the emitted key-value pair from the Mapper and does the sorting and shuffling. 
After that all the values that have the same TextPair associated with them is put in the iterable list.
This value is then provided to the Reducer. In Reducer we just add the values in the list,
just as we had done in case of the wordCount.

Finally, we will code the driver class that controls the job. Here we will need to mention the
MapperOutputKey class as TextPair.class, which is the custom writable class.


Setup the input directory in HDFS
----------------------------------
Download ebooks from Project Gutenberg(http://www.gutenberg.org/). Save the ebook as plain text in a directory with the name ‘input’.

Later, we need to move this directory in HDFS. To do that, type the following in the terminal:

1$ hadoop-1.1.2/bin/hadoop fs -put ~/Desktop/input/ .

This will move the directory in HDFS as seen below.

$ hadoop-1.1.2/bin/hadoop fs -ls

Found 1 items
drwxr-xr-x   - hadoop supergroup          0 2013-11-20 23:13 /user/hadoop/input



Run the job
------------
$ hadoop-1.1.2/bin/hadoop jar ~/Desktop/bigramCount.jar BigramCount input output
13/11/20 23:13:28 WARN mapred.JobClient: Use GenericOptionsParser for parsing the arguments. Applications should implement Tool for the same.
13/11/20 23:13:28 INFO input.FileInputFormat: Total input paths to process : 1
13/11/20 23:13:28 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
13/11/20 23:13:28 WARN snappy.LoadSnappy: Snappy native library not loaded
13/11/20 23:13:28 INFO mapred.JobClient: Running job: job_201311202308_0003
13/11/20 23:13:29 INFO mapred.JobClient:  map 0% reduce 0%
13/11/20 23:13:35 INFO mapred.JobClient:  map 100% reduce 0%
13/11/20 23:13:43 INFO mapred.JobClient:  map 100% reduce 33%
13/11/20 23:13:45 INFO mapred.JobClient:  map 100% reduce 100%
13/11/20 23:13:46 INFO mapred.JobClient: Job complete: job_201311202308_0003
13/11/20 23:13:46 INFO mapred.JobClient: Counters: 26
13/11/20 23:13:46 INFO mapred.JobClient:   Job Counters
13/11/20 23:13:46 INFO mapred.JobClient:     Launched reduce tasks=1
13/11/20 23:13:46 INFO mapred.JobClient:     SLOTS_MILLIS_MAPS=5779
13/11/20 23:13:46 INFO mapred.JobClient:     Total time spent by all reduces waiting after reserving slots (ms)=0
13/11/20 23:13:46 INFO mapred.JobClient:     Total time spent by all maps waiting after reserving slots (ms)=0
13/11/20 23:13:46 INFO mapred.JobClient:     Launched map tasks=1
13/11/20 23:13:46 INFO mapred.JobClient:     Data-local map tasks=1
13/11/20 23:13:46 INFO mapred.JobClient:     SLOTS_MILLIS_REDUCES=9545
13/11/20 23:13:46 INFO mapred.JobClient:   File Output Format Counters
13/11/20 23:13:46 INFO mapred.JobClient:     Bytes Written=343198
13/11/20 23:13:46 INFO mapred.JobClient:   FileSystemCounters
13/11/20 23:13:46 INFO mapred.JobClient:     FILE_BYTES_READ=803716
13/11/20 23:13:46 INFO mapred.JobClient:     HDFS_BYTES_READ=274173
13/11/20 23:13:46 INFO mapred.JobClient:     FILE_BYTES_WRITTEN=1711913
13/11/20 23:13:46 INFO mapred.JobClient:     HDFS_BYTES_WRITTEN=343198
13/11/20 23:13:46 INFO mapred.JobClient:   File Input Format Counters
13/11/20 23:13:46 INFO mapred.JobClient:     Bytes Read=274059
13/11/20 23:13:46 INFO mapred.JobClient:   Map-Reduce Framework
13/11/20 23:13:46 INFO mapred.JobClient:     Map output materialized bytes=803716
13/11/20 23:13:46 INFO mapred.JobClient:     Map input records=4893
13/11/20 23:13:46 INFO mapred.JobClient:     Reduce shuffle bytes=803716
13/11/20 23:13:46 INFO mapred.JobClient:     Spilled Records=93962
13/11/20 23:13:46 INFO mapred.JobClient:     Map output bytes=709748
13/11/20 23:13:46 INFO mapred.JobClient:     Total committed heap usage (bytes)=269619200
13/11/20 23:13:46 INFO mapred.JobClient:     Combine input records=0
13/11/20 23:13:46 INFO mapred.JobClient:     SPLIT_RAW_BYTES=114
13/11/20 23:13:46 INFO mapred.JobClient:     Reduce input records=46981
13/11/20 23:13:46 INFO mapred.JobClient:     Reduce input groups=24292
13/11/20 23:13:46 INFO mapred.JobClient:     Combine output records=0
13/11/20 23:13:46 INFO mapred.JobClient:     Reduce output records=24292
13/11/20 23:13:46 INFO mapred.JobClient:     Map output records=46981


You can now view the output from HDFS itself or download the directory on the local hard disk using the get command.

The output would look similar to the following:


...

command of  4

command the 1

commanded by    4

commanded the   1

commanded with  2

commander 10    1

commander Colonel   2

commander General   1

commander Prince    1

commander dated 1

commander decided   1

commander hastily   1

commander of    8

commander sent  1
...