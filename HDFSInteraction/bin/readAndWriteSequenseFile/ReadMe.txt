HadoopClient.java handles reading, writing and copying Hadoop Sequence Files on local or remote Hadoop file systems (HDFS).


If you are using Eclipse for development and testing like I do, you need to add the following step, so you can compress your sequence file using GZip.

If you notice, I am using Hadoop by Cloudera in my pom file. To use GZip, I need to add a native library to my development environment which is Ubuntu 12.10.

sudo apt-get update; sudo apt-get install hadoop

This will install Hadoop native libraries in /usr/lib/hadoop/lib/native. Now, In Eclipse, edit ->Properties->Java Build Path->Libraries->Maven Dependencies->Native library location, and set "Location Path" to /usr/lib/hadoop/lib/native.
Please make sure the version of Hadoop client dependecy you use in your pom file matches the version of Hadoop you downloaded to your system, otherwise you will get a run time error:

ERROR nativeio.NativeIO: Unable to initialize NativeIO libraries

To verify a sequence file was created on HDFS, log into one of your hadoop nodes and run this command:

hadoop fs -ls /tmp/nb.sgz

And, if you run into a problem and need to see what Hadoop is doing, turn on debugging for Hadoop classes by adding the following entry to your log4j.properties: #Turn on hadoop logging


log4j.logger.org.apache.hadoop=DEBUG
