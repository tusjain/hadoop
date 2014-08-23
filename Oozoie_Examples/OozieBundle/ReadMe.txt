Bundle is a higher-level oozie abstraction that will batch a set of coordinator applications. 
The user will be able to start/stop/suspend/resume/rerun in the bundle level resulting a better
and easy operational control.

More specifically, the oozie Bundle system allows the user to define and execute a bunch of
coordinator applications often called a data pipeline. There is no explicit dependency among
the coordinator applications in a bundle. However, a user could use the data dependency of 
coordinator applications to create an implicit data application pipeline.

The sample bundle application is time triggered.  The start time is defined in the bundle 
job.properties file.  The bundle application starts two coordinator applications- as defined
 in the bundle definition file - bundleConfirguration.xml.

The first coordinator job is time triggered.  The start time is defined in the bundle 
job.properties file.  It runs a workflow, that includes a java main action.  The java program 
parses some log files and generates a report.  The output of the java action is a dataset 
(the report) which is the trigger for the next coordinator job.

The second coordinator job gets triggered upon availability of the file _SUCCESS in the output 
directory of the workflow application of the first coordinator application.  It executes a 
workflow that has a sqoop action;  The sqoop action pipes the output of the first coordinator
job to a mysql database.

Includes:
---------
Sample data defintion and structure 01-SampleDataAndStructure
Data and script download: 02-DataAndScriptDownload
Data load commands: 03-HdfsLoadCommands
Mysql database setup: 04-mysqlDBSetup
Sqoop task -standalone tryout: 05-SqoopStandAloneTryout
Oozie configuration for email: 06-OozieSMTPconfiguration
Bunle job properties file: 07-BundleJobProperties
Bundle definition file: 08-BundleXML
Coordinator defintion -LogParser: 09-CoordinatorXMLLogParser
Workflow defintion -LogParser: 10-WorkflowXMLLogParser
Independent test of LogParser jar: 11-LogParserStandaloneTestHowTo
Coordinator defintion -DataExporter: 12-CoordinatorXMLDataExporter
Workflow defintion -DataExporter: 13-WorkflowXMLDataExporter
Oozie commands: 14-OozieJobExecutionCommands
Output of LogParser: 15a-OutputLogParser
Output in mysql: 15b-OutputDataExporter
Oozie web console - screenshots: 16-OozieWebConsoleScreenshots
Java LogParser code:   17 Java Code


01a. Sample data
-----------------
May 3 11:52:54 cdh-dn03 init: tty (/dev/tty6) main process (1208) killed by TERM signal
May 3 11:53:31 cdh-dn03 kernel: registered taskstats version 1
May 3 11:53:31 cdh-dn03 kernel: sr0: scsi3-mmc drive: 32x/32x xa/form2 tray
May 3 11:53:31 cdh-dn03 kernel: piix4_smbus 0000:00:07.0: SMBus base address uninitialized - upgrade BIOS or use force_addr=0xaddr
May 3 11:53:31 cdh-dn03 kernel: nf_conntrack version 0.5.0 (7972 buckets, 31888 max)
May 3 11:53:57 cdh-dn03 kernel: hrtimer: interrupt took 11250457 ns
May 3 11:53:59 cdh-dn03 ntpd_initres[1705]: host name not found: 0.rhel.pool.ntp.org
01b. Structure
---------------
Month = May
Day = 3
Time = 11:52:54
Node = cdh-dn03
Process = init:
Log msg = tty (/dev/tty6) main process (1208) killed by TERM signal

Directory structure
-------------------
oozieProject
	data
		kashit-syslog
			<<Node-Name>>
				<<Year>>
					<<Month>>
						messages
	bundleApplication
		job.properties
		bundleConfiguration.xml
		 
		coordAppLogParser
			coordinator.xml
			workflowAppLogParser
				workflow.xml
				lib
					LogEventCount.jar
		 
		coordAppDataExporter
			coordinator.xml
			workflowAppDataExporter
				workflow.xml

				
03-Hdfs load commands
----------------------
$ hadoop fs -mkdir oozieProject
$ hadoop fs -put oozieProject/* oozieProject/

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



Tryout the sqoop task- outside of workflow
-------------------------------------------
Use the dataset from my gist-
https://gist.github.com/kashit/5970026
 
 
*********************************
Sqoop command
*********************************
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
</property


#*******************************************
# LogParser program - standalone test
#*******************************************
 
Commands to test the java program
 
a) Command to run the program
$ $ hadoop jar oozieProject/bundleApplication/coordAppLogParser/workflowAppLogParser/lib/LogEventCount.jar Airawat.Oozie.Samples.LogEventCount "oozieProject/data/*/*/*/*/*" "oozieProject/bundleApplication/coordAppLogParser/workflowAppLogParser/myCLIOutput"
b) Command to view results
$ hadoop fs -cat oozieProject/bundleApplication/coordAppLogParser/workflowAppLogParser/myCLIOutput/part* | sort
c) Results
2013-NetworkManager 7
22013-console-kit-daemon 7
2013-gnome-session 11
2013-init 166
2013-kernel 810
2013-login 2
2013-NetworkManager 7
2013-nm-dispatcher.action 4
2013-ntpd_initres 4133
2013-polkit-agent-helper-1 8
2013-pulseaudio 18
2013-spice-vdagent 15
2013-sshd 6
2013-sudo 8
2013-udevd 6


****************************************
14. Oozie job commands
****************************************
 
Note: Replace oozie server and port, with your cluster-specific.
1) Submit job:
$ oozie job -oozie http://cdh-dev01:11000/oozie -config oozieProject/bundleApplication/job.properties -submit
job: 0000012-130712212133144-oozie-oozi-W
2) Run job:
$ oozie job -oozie http://cdh-dev01:11000/oozie -start 0000014-130712212133144-oozie-oozi-W
3) Check the status:
$ oozie job -oozie http://cdh-dev01:11000/oozie -info 0000014-130712212133144-oozie-oozi-W
4) Suspend workflow:
$ oozie job -oozie http://cdh-dev01:11000/oozie -suspend 0000014-130712212133144-oozie-oozi-W
5) Resume workflow:
$ oozie job -oozie http://cdh-dev01:11000/oozie -resume 0000014-130712212133144-oozie-oozi-W
6) Re-run workflow:
$ oozie job -oozie http://cdh-dev01:11000/oozie -config oozieProject/bundleApplication/job.properties -rerun 0000014-130712212133144-oozie-oozi-W
7) Should you need to kill the job:
$ oozie job -oozie http://cdh-dev01:11000/oozie -kill 0000014-130712212133144-oozie-oozi-W
8) View server logs:
$ oozie job -oozie http://cdh-dev01:11000/oozie -logs 0000014-130712212133144-oozie-oozi-W
Logs are available at:
/var/log/oozie on the Oozie server.

****************************************
Output - Log Parser program
****************************************
 
$ hadoop fs -cat oozieProject/bundleApplication/coordAppLogParser/workflowAppLogParser/output/part*
 
2013-NetworkManager 7
22013-console-kit-daemon 7
2013-gnome-session 11
2013-init 166
2013-kernel 810
2013-login 2
2013-NetworkManager 7
2013-nm-dispatcher.action 4
2013-ntpd_initres 4133
2013-polkit-agent-helper-1 8
2013-pulseaudio 18
2013-spice-vdagent 15
2013-sshd 6
2013-sudo 8
2013-udevd 6


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

