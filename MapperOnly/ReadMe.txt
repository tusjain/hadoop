In the word count example (word-count-hadoop-example) we can see two set of parallel process, a map and a reduce. 
In reduce process basically what happens is an aggregation of values or rather an operation on values that share 
the same key. Now consider a scenario where we have a just a parallel process to be performed no aggregation required. 
Here we go in for the map operation and no reduce, ie we’d explicitly set the number of reducers to zero.

Why set reducers to Zero? : We know that between map and reduce phases there is a key phase, the sort and shuffle. 
  Sort and Shuffle is responsible for sorting the keys in ascending order and then grouping values based on same keys.
  This phase is really expensive and if reduce phase is not required we should avoid it as avoiding reduce phase would
  eliminate sort and shuffle phase as well. In order to eliminate reduce phase we have to explicitly set 
  mapred.reduce.tasks=0 because by default there would be a value set for your cluster by your admin in conf/mapred-site.xml.

An example Business scenario: It is just a mock scenario just to explain one possibility of Hadoop usage in mere parallel 
  processing scenario. It needn’t be a eligible use case for Hadoop.

Problem Statement: We have a database dump (extract) with us which contains the following details related to an employee
  as Employee ID, Employee Name, Location Id, Grade Id, Monthly Pay, Days Worked in a Month and No of holidays in that month.
  We need to calculate the monthly salary based on the no of days worked by that employee. (Just imagine that the file size
  is huge).

Proposed Solution: Since we need to calculate the salary for such large data set and the data being independent chunks (i.e.
  we can process line by line, data on one line is independent chunk) we can go for Hadoop.

Input File - Employee.txt : A two line sample of input file (db extract) is given.
10007~James Wilson~L105~G06~110000~22~8
10100~Roger Williams~L103~G09~145000~20~8

How to run the map reduce job? : Follow the steps in order to run the job
1. Pack these 3 files into a jar (salary calc.jar) and copy the same into some location in LFS(/home/bejoys/samples/)
2. Copy the input file into some location in LFS (/home/bejoys/samples/employee.txt)
3. Copy the input into HDFS

Hadoop fs – copyFromLocal /home/input/input.txt  /userdata/input/

4. Run the jar

hadoop jar /home/salarycalc.jar SalaryCalculatorDriver /userdata/input/ /userdata/output/

5. Retrieve the output on to LFS

hadoop fs -getmerge /userdata/output /home/salcal_output.txt


Do we need a reducer here?
No we don’t. In fact you don’t even need the SalaryCalculatorReducer class, but my merely avoiding this class and not 
setting the reducer class in Job object won’t do your job. (Commenting this line of code won’t serve the purpose 
job.setReducerClass(SalaryCalculatorReducer.class)). Even if you don’t set your reducer class here a default reducer would 
be fired by the map reduce job that matches and the output key and value types from your mapper.

How can we avoid the reduce operation?
Just the number of reduce tasks to zero, you can do it in two ways
1.  Alter your driver class to add a line of code as job.setNumReduceTasks(0);
2.  Altering the code and they packing it as jar is little time consuming so we can do the same in command line itself 
    during run time. Alter your Hadoop jar command to include the number of reducers as well
    
hadoop jar /home/salarycalc.jar SalaryCalculatorDriver –D mapred.reduce.tasks=0 /userdata/input/ /userdata/output/


Do I have to specify an empty text as the mapper/reducer value?