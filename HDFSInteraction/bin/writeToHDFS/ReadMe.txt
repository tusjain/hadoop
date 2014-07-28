Q) How to write file data to HDFS using java program?

CopyFileToHDFS reads the data from source file which resides in the clients local file system and writes 
the data to the HDFS. It also prints the progress of the write operation on the output console.


hdfs://localhost:54310/ is the URI of HDFS.
/user/hadoop/Hadoop_File.txt is the target hadoop file.
/home/client/localsystem/file/path/sample.txt is the source file resides in the clients local file system.

This program was tested in pseudo cluster mode. 