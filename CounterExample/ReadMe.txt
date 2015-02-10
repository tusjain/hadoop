Counters are very useful feature in hadoop. This helps us in tracking global events in our job, ie across map and reduce phases.
When we execute a mapreduce job, we can see a lot of counters listed in the logs. Other than the default built-in counters, 
we can create our own custom counters. The custom counters will be listed along with the built-in counters.

This helps us in several ways. Here I am explaining a scenario where I am using a custom counter for counting the number of 
good words and stop words in the given text files. The stop words in this program are provided at the run time using distributed cache.
This is a mapper only job. The property job.setNumReduceTasks(0) makes the it a mapper only job.

Distributed cache will distribute application specific read only files efficiently through out the application.
My requirement is to filter the stop words from input text files. The stop words list may vary. So if I hard code the 
list in my program, I have to update the code everytime to make changes in the stop word list. This is not a good practice. 
I used distributed cache for this and the file containing the stop words is loaded to the distributed cache. This makes the file 
available to mapper as well as reducer. In this program, we don’t require any reducer.

Export the project as a runnable jar and execute. The file containing the stop words should be present in hdfs. The stop words should be added line by line in the stop word file. Sample format is given below.

is

the

am

are

with
m
was

were

Sample command to execute the program is given below.

hadoop jar <jar-name>  -skip  <stop-word-file-in hdfs>   <input-data-location>    <output-location>

Eg:  hadoop jar Skipper.jar  -skip /user/hadoop/skip/skip.txt     /user/hadoop/input     /user/hadoop/output

In the job logs, you can see the custom counters also.