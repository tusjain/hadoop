The coordinator application has a start time, and when the start time condition is met, 
it will transition to waiting state where it will look for the availability of a dataset.  
Once the dataset is available, it will run the workflow specified.

This gist includes components of a oozie, dataset availability initiated, coordinator job -
scripts/code, sample data and commands; Oozie actions covered: hdfs action, email action,
sqoop action (mysql database); Oozie controls covered: decision.

Usecase
-------
Pipe report data available in HDFS, to mysql database;

Includes:
---------
Data and script download: 02-DataAndScriptDownload
Data load commands: 03-HdfsLoadCommands
Mysql database setup: 04-mysqlDBSetup
Sqoop command - test: 05-SqoopStandaloneTryout
Oozie configuration for email 06-OozieSMTPconfiguration
Oozie coorindator properties file 07-OozieCoordinatorProperties
Oozie cooridinator conf file 08-OozieCoordinatorXML
Sqoop workflow conf file 09-SqoopWorkflowXML
Oozie commands 10-OozieJobExecutionCommands
Output in mysql 11-Output

Directory structure
*********************************
oozieProject
	sampleCoordinatorJobDatasetDep
		coordinatorConf/
			coordinator.properties
			coordinator.xml
		sqoopWorkflowApp
			workflow.xml
	 
	datasetGeneratorApp
		outputDir
			part-r-00000
			_SUCCESS
			_logs
				history
					cdh-jt01_1372261353326_job_201306261042_0536_conf.xml
					job_201306261042_0536_1373407670448_akhanolk_Syslog+Event+Rollup
					
The datasetGeneratorApp is essentially what we will use to trigger the coordinator job.  
While the logs are not important for the simulation, the presence of _SUCCESS is needed, 
failing which the job will not get triggered.

*********************************
Hdfs data load commands
*********************************
 
$ hadoop fs -mkdir oozieProject
$ hadoop fs -put oozieProject/* oozieProject/
 
Run command below to validate load against expected directory structure in section 02-DataAndScriptDownload
$ hadoop fs -ls -R oozieProject/sampleCoordinatorJobDatasetDep | awk '{print $8}'
Remove the dataset directory - we will load it when we want to trigger the job
$ hadoop fs -rm -R oozieProject/datasetGeneratorApp

*********************************
Mysql database setup tasks
*********************************
 
a) Create database:
 
mysql>
create database kashit;
 
b) Switch to database created:
 
mysql>
use kashit;
 
c) Create destination table for sqoop export from hdfs:
 
mysql>
CREATE TABLE IF NOT EXISTS Logged_Process_Count_By_Year(
year_and_process varchar(100),
occurrence INTEGER);
 
d) Ensure your sqoop user has access to database created:
 
mysql>
grant all on kashit.* to myUser@'myMachine';

*************************************************************
Sqoop command - try out the command to see if it works
*************************************************************
 
Pre-requisties:
1. Dataset to be exported should exist on HDFS
2. mySql table that is the destination for the export should exist
 
Command:
--Run on node that acts as sqoop client;
 
$ sqoop export \
--connect jdbc:mysql://cdh-dev01/kashit \
--username devUser \
--password myPwd \
--table Logged_Process_Count_By_Year \
--direct \
--export-dir "oozieProject/datasetGeneratorApp/outputDir" \
--fields-terminated-by "\t"
 
 
*********************************
Results in mysql
*********************************
 
mysql> select * from Logged_Process_Count_By_Year order by occurrence desc;
+----------------------------+------------+
| year_and_process | occurrence |
+----------------------------+------------+
| 2013-ntpd_initres | 4133 |
| 2013-kernel | 810 |
| 2013-init | 166 |
| 2013-pulseaudio | 18 |
| 2013-spice-vdagent | 15 |
| 2013-gnome-session | 11 |
| 2013-sudo | 8 |
| 2013-polkit-agent-helper-1 | 8 |
| 2013-console-kit-daemon | 7 |
| 2013-NetworkManager | 7 |
| 2013-udevd | 6 |
| 2013-sshd | 6 |
| 2013-nm-dispatcher.action | 4 |
| 2013-login | 2 |
+----------------------------+------------+
14 rows in set (0.00 sec)
 
*************************************************
--Cleanup
*************************************************
mysql>
delete from Logged_Process_Count_By_Year;


*************************
Oozie SMTP configuration
*************************
The following needs to be added to oozie-site.xml - after updating per your environment and configuration;
<!-- SMTP params-->
<property>
<name>oozie.email.smtp.host</name>
<value>cdh-dev01</value>
</property>
<property>
<name>oozie.email.smtp.port</name>
<value>25</value>
</property>
<property>
<name>oozie.email.from.address</name>
<value>oozie@cdh-dev01</value>
</property>
<property>
<name>oozie.email.smtp.auth</name>
<value>false</value>
</property>
<property>
<name>oozie.email.smtp.username</name>
<value></value>
</property>
<property>
<name>oozie.email.smtp.password</name>
<value></value>
</property>

****************************************
Oozie job commands
****************************************
 
a) Prep
Modify the start-end time of the job in the coordinator properties file, as needed.
Then run the following command.
 
b) Submit job
$ oozie job -oozie http://cdh-dev01:11000/oozie -config oozieProject/sampleCoordinatorJobDatasetDep/coordinatorConf/coordinator.properties -run
 
A job ID is displayed.
The job shuld not trigger till the dataset is loaded.
It should be in waiting state - see oozie web console screenshots in the last section.
 
If you need to kill the job...
$ oozie job -oozie http://cdh-dev01:11000/oozie -kill <<Job ID>>
 
c) Publish trigger
Ideally, this would be generated after some map reduce job completed.
For simplicity, I have provided the output of one of the jobs from one of my blogs/gists.
 
$ hadoop fs -put oozieProject/datasetGeneratorApp/ oozieProject/

****************************************
Output - data export from hdfs to mysql
****************************************
 
mysql> select * from Logged_Process_Count_By_Year order by occurrence desc;
+----------------------------+------------+
| year_and_process | occurrence |
+----------------------------+------------+
| 2013-ntpd_initres | 4133 |
| 2013-kernel | 810 |
| 2013-init | 166 |
| 2013-pulseaudio | 18 |
| 2013-spice-vdagent | 15 |
| 2013-gnome-session | 11 |
| 2013-sudo | 8 |
| 2013-polkit-agent-helper-1 | 8 |
| 2013-console-kit-daemon | 7 |
| 2013-NetworkManager | 7 |
| 2013-udevd | 6 |
| 2013-sshd | 6 |
| 2013-nm-dispatcher.action | 4 |
| 2013-login | 2 |
+----------------------------+------------+
14 rows in set (0.00 sec)


