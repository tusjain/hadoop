MaReduce Program using compression in MapReduce
++++++++++++++++++++++++++++++++++++++++++++++++++

Step 1: Prepare input file; that is, compress to gz/snappy/....   ---> gzip data.txt   snzip data.txt
Step 2: Compile java files & make jar file 
Step 3: Upload compressed input file to HDFS

hadoop fs -put data.txt.gz
 
 OR
 
hadoop fs -put data.txt.snz
  
Step 4: run MapReduce with the uploaded input file

hadoop jar MaxTemperatureWithCompression.jar MaxTemperatureWithCompression data.txt.gz output

Step 4: check output