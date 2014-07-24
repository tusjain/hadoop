Joining input files Hadoop is not a recommended way, you have better tools like Pig & Hive for the same. 
Nevertheless, here is an example of MapReduce utilizing multiple input files.
                

Problem Statement
====================
A retailer has a customer data base and he need to do some promotions based on the same. 
He chooses bulk sms as the choice of his promotion which is done by a third part for him. And once the sms
 is pushed the sms provider returns the delivery status back to the retailer. Now let us look into more 
 details which makes things a little complicated.
 
We have 3 input files as follows

1. UserDetails.txt: Every record is of the format ‘mobile number , consumer name’
2. DeliveryDetails.txt: Every record is of the format ‘mobile number, delivery status code’
3. DeliveryStatusCodes.txt: Every record is of the format ‘delivery status code, status message’

The retailer has a consumer data base(UserDetails.txt) from which only the mobile number are provided 
to a bulk sms provider. He can’t reveal the customer name due to security reasons. Once the messages 
are pushed the sms provider sends back a report of the mobile numbers with status code (DeliveryDetails.txt) 
and also a look up file that relates every status code to the corresponding Status message (DeliveryStatusCodes.txt).

The requirement is that for meaningful information we need the consumer name along with its corresponding
status message. And we need to obtain the same from these 3 files.

Sample Inputs
++++++++++++
File 1 – UserDetails.txt
--------------------------
123 456, Jim
456 123, Tom
789 123, Harry
789 456, Richa

File 2 – DeliveryDetails.txt
--------------------------------
123 456, 001
456 123, 002
789 123, 003
789 456, 004

File 3 – DeliveryStatusCodes.txt
--------------------------------
001, Delivered
002, Pending
003, Failed
004, Resend

Expected Output
+++++++++++++++++++
Jim, Delivered
Tom, Pending
Harry, Failed
Richa, Resend


Solution 
============
1. Use two different mapper classes for both processing the  initial inputs from UserDetails.txt 
and DeliveryDetails.txt, The Key value output from the mappers should be as follows
a) UserDetails.txt
           i. Key(Text) – mobile number
           ii. Value(Text) – An identifier to indicate the source of input(using ‘CD’ for the customer
               details file) + Customer Name
               
b)DeliveryDetails.txt
           i. Key(Text) – mobile number
           ii.Value(Text) – An identifier to indicate the source of input(using ‘DR’ for the delivery r
              eport file) + Status Code
              
So here since the two files needs to be parsed separately using two mappers. I’m using UserFile Mapper.java to 
process UserDetails.txt and DeliveryFileMapper.java to process DeliveryDetails.txt

In map reduce API, I’m using MulipleInputFormat to specify which input to go into which mapper. But the output 
key value pairs from the mapper go into the same reducer, for the Reducer to identify the source of the value 
we are prepending the values ‘CD’ or ‘DR’.

2. On the reducer end use distributed cache to distribute the DeliveryStatusCodes.txt. Parse the file and load the 
contents into HashMap with Key being the status code and value being the status message

3. On the reducer every key would be having two values one with prefix ‘CD’ and other ‘DR’. (For simplicity let us 
assume only 2 values, in real time it can be more). Identify the records and from CD get the customer name corresponding 
to the cell number (input key) and from DR get the status code. On obtaining the status code do a look up on the 
HashMap to get the status message. So finally the output Key values from the reducer would be as follows:

	a) Key : Customer Name
	b) Value : Status Message
	
	
++++++++++++++++++++++++++++
Let us go in for a small code walk through:

1. The only difference in the code is that we are using MultipleInputFormat instead of FileInputFormat. 
   This is necessary as we use two mappers and we need the output of the two mappers to be processed by a single reducer
2. When we normally execute our map reduce with the hadoop jar command the last two arguments on the 
   command line represent the input and output dir in hdfs. But here instead of two we’d have three input locations 
   and one output location.
3. The second key thing to be noted here is that in place of input locations don’t provide the full path with file 
   names. Provide the input directories instead. Load the two files in two separate directories and provide the 
   corresponding paths to mappers.
4. Since driver is getting the arguments from command line, the order of arguments is also very critical. Make sure 
   that the input directories always point to their corresponding mappers itself.

You can run the above example with the following command on CLI as

hadoop jar /home/bejoys/samples/ smsMarketing.jar com.bejoy.samples.smsmarketing.SmsDriver  -files /home/bejoys/samples/ DeliveryStatusCodes.txt /userdata/bejoys/samples/sms/consumerdata /userdata/bejoys/samples/sms/deliveryinformation /userdata/bejoys/samples/sms/output  

Note:
     i. Since the join the happening on reduce, it is termed as a reduce side join.
     ii. This is a very basic approach to implement joins in map reduce and is for those who have a basic 
         knowledge on map reduce programming. You can implement it in more sophisticated manner in mapreduce 
         frame work using DataJoin Mappers and Reducers with TaggedMap Output Types.

But if it is a join then I’d strongly recommend you to go in with Pig or Hive as both of these are highly optimized for 
implementing joins. Also you can eliminate the coding effort you need to put in. It is not exaggerating if I say I can 
implement the same in a single step using hive. Let us just check it out

Using Hive
1. Load the data into 3 hive tables
2. Perform join using hive QL

Creating Hive tables to store the files
CREATE TABLE customer_details (cellNumber String,consumerName String)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ',';

LOAD DATA LOCAL INPATH '/home/bejoys/samples/ConsumerDetails.txt' INTO TABLE customer_details;

CREATE TABLE delivery_report (cellNumber String,statusCode int)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ',';

LOAD DATA LOCAL INPATH '/home/bejoys/samples/DeliveryReport.txt' INTO TABLE delivery_report;

CREATE TABLE status_codes (statusCode int,statusMessage String)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ',';

LOAD DATA LOCAL INPATH '/home/bejoys/samples/DeliveryStatusCodes.txt' INTO TABLE status_codes;

Hive Query to execute Join operation on data sets

Select cd.consumerName,sc.statusMessage FROM customer_details cd
JOIN delivery_report dr ON (cd.cellNumber = dr.cellNumber) JOIN
status_codes sc ON(dr.statusCode = sc.statusCode);

You can optimize the hive Query again for performance boosting.