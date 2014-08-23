This gist includes components of a oozie workflow application - scripts/code, sample data
and commands; Oozie actions covered: sub-workflow, email java main action,
sqoop action (to mysql); Oozie controls covered: decision;
 
Pictorial overview:
--------------------
http://hadooped.blogspot.com/2013/07/apache-oozie-part-8-subworkflow.html
 
Usecase:
--------
Parse Syslog generated log files to generate reports; Export reports to RDBMS;
 
Includes:
---------
Sample data defintion and structure 01-SampleDataAndStructure
Data and script download: 02-DataAndScriptDownload
Data load commands: 03-HdfsLoadCommands
Mysql database setup: 04-mysqlDBSetup
Sqoop task -standalone tryout: 05-SqoopStandAloneTryout
App job properties file: 06-JobProperties
Workflow defintion -Parent: 07-WorkflowXMLMain
Independent test of LogParser jar: 08-LogParserStandaloneTestHowTo
Workflow defintion -DataExporter: 09-SubWorkflowXMLDataExporter
Oozie commands: 10-OozieJobExecutionCommands
Output of LogParser: 11a-OutputLogParser
Output in mysql: 11b-OutputDataExporter
Java LogParser code: 12-JavaCodeHyperlink

01a. Sample data (log files)
----------------------------
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
	workflowWithSubworkflow
		job.properties
		workflow.xml
		lib
			LogEventCount.jar
 
		dataExporterSubWorkflowApp
			workflow.xml
			
-Hdfs load commands
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

#*******************************************
# LogParser program - standalone test
#*******************************************
Commands to test the java program
a) Command to run the program
$ $ hadoop jar oozieProject/workflowWithSubworkflow/lib/LogEventCount.jar Airawat.Oozie.Samples.LogEventCount "oozieProject/data/*/*/*/*/*" "oozieProject/workflowWithSubworkflow/myCLIOutput"
b) Command to view results
$ hadoop fs -cat oozieProject/workflowWithSubworkflow/myCLIOutput/part* | sort
c) Results
2013-NetworkManager 7
2013-console-kit-daemon 7
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
10. Oozie job commands
****************************************
Note: Replace oozie server and port, with your cluster-specific.
1) Submit job:
$ oozie job -oozie http://cdh-dev01:11000/oozie -config oozieProject/workflowWithSubworkflow/job.properties -submit
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
$ oozie job -oozie http://cdh-dev01:11000/oozie -config oozieProject/workflowWithSubworkflow/job.properties -rerun 0000014-130712212133144-oozie-oozi-W
7) Should you need to kill the job:
$ oozie job -oozie http://cdh-dev01:11000/oozie -kill 0000014-130712212133144-oozie-oozi-W
8) View server logs:
$ oozie job -oozie http://cdh-dev01:11000/oozie -logs 0000014-130712212133144-oozie-oozi-W
Logs are available at:
/var/log/oozie on the Oozie server.


****************************************
Output - Log Parser program
****************************************
$ hadoop fs -cat oozieProject/workflowWithSubworkflow/output/part*
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



