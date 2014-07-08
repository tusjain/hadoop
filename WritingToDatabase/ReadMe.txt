The DBInputFormat and DBOutputFormat component provided in Hadoop 0.19, allows easy import and export of data between Hadoop and many relational databases,
allowing relational data to be more easily incorporated into your data processing pipeline.

To import and export data between Hadoop and MySQL, you surely need Hadoop, MySQL installation on your machine.


To access the data from DB we have to create a class to define the data which we are going to fetch and write back to DB. 
In this project DBInputWritable.java and DBOutputWritable.java do the job.


Database and table creation in MySQL

mysql> use testDb;

mysql> create table studentinfo (  id integer ,  name varchar(32) );

mysql> insert into studentinfo values(1,'Ram');

mysql> insert into studentinfo values(2,'Tom');

mysql> insert into studentinfo values(3,'Bobby');



Download mysql-connector-java-5.0.5.jar file and copy it to in $HADOOP_HOME/lib and restart the Hadoop ecosystem.