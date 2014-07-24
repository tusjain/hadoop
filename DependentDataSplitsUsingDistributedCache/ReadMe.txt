Hadoop is known to process independent data slices, but what about dependent data. Suppose for the processing of employee’s
salary we need data on their location as well as their grades but the location and grade details are available with another file.
So while processing each line of the employee file we need to look up information from location and grade files. How do we 
achieve this? Go for a distributed cache approach.

Distributed Cache
Hadoop has the concept of a distributed cache which all task trackers (nodes) have access to. When we want to distribute some
common data across all task trackers we go for distributed cache. When we need to distribute a file , multiple copies of the 
file would be maintained for all task trackers to access. When we need to look up for some references, the reference 
data/file would be initially posted in the distributed cache. The main point to be noted here is that the files chosen to be 
distributed should be very small. The maximum size of a file to be distributed in a medium range cluster shouldn’t be more than
100MB.(this value could vary from cluster to cluster)

Solution to our Problem
When we consider our scenario say we have 1 million employees across 25 locations spanning across 71 grades. On a very crude
analysis out here, we can see that the location and grade data is relatively too small compared to Employee data. So here our
approach could be like, processing the employee data that is in HDFS with the other two reference data.

For mock calculation purposes I’m implementing the addition of a location bonus and monthly bonus based on grade to all employees
in addition to the basic salary calculation.

So other than the employees.txt file, we do have two more input files.
1. Location.txt which has details like location id, location name and annual  bonus
2. Grade.txt which has details like Grade id , Grade and annual Bonus

Employee.txt
10007~James Wilson~L105~G06~110000~22~8
10100~Roger Williams~L103~G09~145000~20~8

Location.txt
L105,Bangalore,200000
L03,Hyderabad,160000

Grade.txt
G06,D3,450000
G09,F3,500000
+++++++++++++++++++++++++++++++

Reducer Class
No reducer class is required if you don’t need one, during run time the default reducer class would be substituted in map reduce
execution. But the point to be noted here is that when you don’t specify a reducer class the default reducer class instantiated
would have the input and output key value types same as that of the mapper’s output key value types. If you need a different key
value type as reducer output then you need to define your custom reducer.

How to run the map reduce job?: Follow the steps in order to run the job
1. Pack these 2  files into a jar (salary calc.jar) and copy the same into some location in LFS(/home/bejoys/samples/)
2. Copy the input file into some location in LFS (/home/bejoys/samples/input.txt)
3.  Copy the input into HDFS
Hadoop fs – copyFromLocal /home/employee.txt  /userdata/input/
4. Run the jar
 hadoop jar /home/salarycalc.jar SalaryCalculator –files /home/location.txt, /home/grade.txt  –D mapred.reduce.tasks=0 /userdata/input/ /userdata/output/
 
5. Retrieve the output on to LFS
hadoop fs -getmerge /userdata/output /home/salcal_output.txt

Points to be noted here
1. NullWritable: If you look at the example, it is merely for parallel processing and you don’t need a key value concept out 
   here but map reduce programming supports only key value pair programming. Here we treat the whole record as key and since
   we have no value we go in for NullWritable.
   
   Unlike other Writable classes we don’t create a new instance of the same (new NullWritable()) rather we just get an instance
   of the same(NullWritable.get()). This is because unlike other Writables in Hadoop NullWritable is Singleton.

2.  –files
    This option is used during run time to distribute files in cache. Only small files should be placed on distributed cache. 
    When you specify multiple files to be loaded on to distributed cache they have to be specified separated by comma. Make 
    sure that there are no spaces between file names and comma. These files would be retrieved by the task trackers into their 
    local file system before the execution of tasks.
