The coordinator application starts executing upon availability of the trigger file defined and 
initiates the two workflows.  Both workflows generate reports off of data in hdfs.
The java main action parses log files and generates a report.  
The hive actions in the hive sub-workflow run reports off of hive tables against the same log files in hdfs.

Usecase
-------
Parse Syslog generated log files to generate reports;
 
Includes:
---------
Sample data and structure: 01-SampleDataAndStructure
Data and script download: 02-DataAndScriptDownload
Data load commands: 03-HdfsLoadCommands
Java MR - Mapper code: 04A-MapperJavaCode
Java MR - Reducer code: 04B-ReducerJavaCode
Java MR - Driver code: 04C-DriverJavaCode
Command to test Java MR program: 04D-CommandTestJavaMRProg
Hive -create log table command 05A-HiveCreateTable
Hive -load partitions 05B-HiveLoadPartitions
Hive commands to test data loaded 05C-HiveDataLoadTestCommands
Hive QL script for report 2 05D-HiveQLReport2
Hive QL script for report 3 05E-HiveQLReport3
Oozie configuration for email 06-OozieSMTPconfiguration
Oozie coorindator properties file 07-OozieCoordinatorProperties
Oozie cooridinator conf file 08-OozieCoordinatorXML
Oozie workflow conf file 09-OozieWorkflowXML
Oozie sub-workflow conf file 10-OozieSubWorkflowXML
Oozie commands 11-OozieJobExecutionCommands
Output -Report1 12A-Rpt1-JavaMainProgramOutput
Output -Report2 12B-Rpt2-HiveProgramOutputIssuesByMonth
Output -Report3 12C-Rpt3-HiveProgramOutputTop3Issues


Sample data
------------
May 3 11:52:54 cdh-dn03 init: tty (/dev/tty6) main process (1208) killed by TERM signal
May 3 11:53:31 cdh-dn03 kernel: registered taskstats version 1
May 3 11:53:31 cdh-dn03 kernel: sr0: scsi3-mmc drive: 32x/32x xa/form2 tray
May 3 11:53:31 cdh-dn03 kernel: piix4_smbus 0000:00:07.0: SMBus base address uninitialized - upgrade BIOS or use force_addr=0xaddr
May 3 11:53:31 cdh-dn03 kernel: nf_conntrack version 0.5.0 (7972 buckets, 31888 max)
May 3 11:53:57 cdh-dn03 kernel: hrtimer: interrupt took 11250457 ns
May 3 11:53:59 cdh-dn03 ntpd_initres[1705]: host name not found: 0.rhel.pool.ntp.org
Structure
----------
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
	 
	sampleCoordinatorJobTrigFileDep
		triggerDir
			trigger.dat
		 
		coordinatorConf/
			coordinator.properties
			coordinator.xml
		workflowApp
			workflow.xml
			hiveSubWorkflowApp
				hive-site.xml
				hiveConsolidated-Year-Month-Report.hql
				hiveTop3Processes-Year-Report.hql
				workflow.xml
			lib
				LogEventCount.jar
				
				
Hdfs load commands
------------------
$ hadoop fs -mkdir oozieProject
$ hadoop fs -put oozieProject/data oozieProject/
$ hadoop fs -put oozieProject/sampleCoordinatorJobTrigFileDep oozieProject/
Run command below to validate load against expected directory structure in section 02-DataAndScriptDownload
$ hadoop fs -ls -R oozieProject/sampleCoordinatorJobTrigFileDep | awk '{print $8}'
 
Remove the trigger file directory - we will load it when we want to execute the job
$ hadoop fs -rm -R oozieProject/sampleCoordinatorJobTrigFileDep/triggerDir/

Commands to test the java program
---------------------------------
a) Command to run the program
$ hadoop jar oozieProject/sampleCoordinatorJobTrigFileDep/workflowApp/lib/LogEventCount.jar Airawat.Oozie.Samples.LogEventCount "oozieProject/sampleCoordinatorJobTrigFileDep/data/*/*/*/*/*" "oozieProject/sampleCoordinatorJobTrigFileDep/myCLIOutput"
b) Command to view results
$ hadoop fs -cat oozieProject/sampleCoordinatorJobTrigFileDep/myCLIOutput/part*
c) Results
2013-NetworkManager 7
2013-console-kit-daemon 7
2013-gnome-session 11
2013-init 166
2013-kernel 810
2013-login 2
2013-nm-dispatcher.action 4
2013-ntpd_initres 4133
2013-polkit-agent-helper-1 8
2013-pulseaudio 18
2013-spice-vdagent 15
2013-sshd 6
2013-sudo 8
2013-udevd 6


Hive script to create table for logs
-------------------------------------
hive>
CREATE EXTERNAL TABLE SysLogEvents(
month_name STRING,
day STRING,
time STRING,
host STRING,
event STRING,
log STRING)
PARTITIONED BY(node string,year int, month int)
ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.RegexSerDe'
WITH SERDEPROPERTIES (
"input.regex" = "(\\w+)\\s+(\\d+)\\s+(\\d+:\\d+:\\d+)\\s+(\\w+\\W*\\w*)\\s+(.*?\\:)\\s+(.*$)"
)
stored as textfile;


Hive scripts to create and load partitions
-------------------------------------------
Note: Replace my user ID "akhanolk" with yours
hive >
Alter table SysLogEvents Add IF NOT EXISTS partition(node="cdh-dev01",year=2013, month=04)
location '/user/akhanolk/oozieProject/data/kashit-syslog/cdh-dev01/2013/04/';
Alter table SysLogEvents Add IF NOT EXISTS partition(node="cdh-dev01",year=2013, month=05)
location '/user/akhanolk/oozieProject/data/kashit-syslog/cdh-dev01/2013/05/';
Alter table SysLogEvents Add IF NOT EXISTS partition(node="cdh-dn01",year=2013, month=05)
location '/user/akhanolk/oozieProject/data/kashit-syslog/cdh-dn01/2013/05/';
Alter table SysLogEvents Add IF NOT EXISTS partition(node="cdh-dn02",year=2013, month=04)
location '/user/akhanolk/oozieProject/data/kashit-syslog/cdh-dn02/2013/04/';
Alter table SysLogEvents Add IF NOT EXISTS partition(node="cdh-dn02",year=2013, month=05)
location '/user/akhanolk/oozieProject/data/kashit-syslog/cdh-dn02/2013/05/';
Alter table SysLogEvents Add IF NOT EXISTS partition(node="cdh-dn03",year=2013, month=04)
location '/user/akhanolk/oozieProject/data/kashit-syslog/cdh-dn03/2013/04/';
Alter table SysLogEvents Add IF NOT EXISTS partition(node="cdh-dn03",year=2013, month=05)
location '/user/akhanolk/oozieProject/data/kashit-syslog/cdh-dn03/2013/05/';
Alter table SysLogEvents Add IF NOT EXISTS partition(node="cdh-jt01",year=2013, month=04)
location '/user/akhanolk/oozieProject/data/kashit-syslog/cdh-jt01/2013/04/';
Alter table SysLogEvents Add IF NOT EXISTS partition(node="cdh-jt01",year=2013, month=05)
location '/user/akhanolk/oozieProject/data/kashit-syslog/cdh-jt01/2013/05/';
Alter table SysLogEvents Add IF NOT EXISTS partition(node="cdh-nn01",year=2013, month=05)
location '/user/akhanolk/oozieProject/data/kashit-syslog/cdh-nn01/2013/05/';
Alter table SysLogEvents Add IF NOT EXISTS partition(node="cdh-vms",year=2013, month=05)
location '/user/akhanolk/oozieProject/data/kashit-syslog/cdh-vms/2013/05/';


Hive ql to test data loaded
----------------------------
hive>
--Print headers
set hive.cli.print.header=true;
--Need to add this jar for MR to work..your env may not need it
add jar hadoop-lib/hive-contrib-0.10.0-cdh4.2.0.jar;
--Sample query
select * from SysLogEvents limit 2;

--Hive QL script: Generates report
--File name: hiveConsolidated-Year-Month-Report.hql
---------------------------------------------------
use default;
drop table consolidated_YM_report;
CREATE TABLE IF NOT EXISTS consolidated_YM_report(
process string,
node string,
year int,
month int,
occurrence int)
ROW FORMAT DELIMITED
FIELDS TERMINATED by ','
LINES TERMINATED by '\n';
INSERT OVERWRITE TABLE consolidated_YM_report
select case locate('[',event,1) when 0 then case locate(':',event,1) when 0 then event else substr(event,1,(locate(':',event,1))-1) end
else substr(event,1,(locate('[',event,1))-1) end process,Node,Year,Month,Count(*) Occurrence from SysLogEvents group by node,year,month, case locate('[',event,1) when 0 then case locate(
':',event,1) when 0 then event else substr(event,1,(locate(':',event,1))-1) end else substr(event,1,(locate('[',event,1))-1) end order by process asc,node asc,year,month;


--Hive QL script: Generates report
--File name: hiveTop3Processes-Year-Report.hql
---------------------------------------------------
use default;
drop table top3_process_by_year_report;
CREATE TABLE IF NOT EXISTS top3_process_by_year_report(
process string,
year int,
occurrence int)
ROW FORMAT DELIMITED
FIELDS TERMINATED by ','
LINES TERMINATED by '\n';
INSERT OVERWRITE TABLE top3_process_by_year_report
select process, year, occurrence from (select case locate('[',event,1) when 0 then case locate(':',event,1) when 0 then event else substr(event,1,(locate(':',event,1))-1) end else substr
(event,1,(locate('[',event,1))-1) end process,Year,Count(*) Occurrence from SysLogEvents
group by year,case locate('[',event,1) when 0 then case locate(':',event,1) when 0 then event else substr(event,1,(locate(':',event,1))-1) end else substr(event,1,(locate('[',event,1))-1
) end order by process asc,year,Occurrence desc) X where process is not null order by occurrence desc limit 3;


Oozie SMTP configuration
------------------------
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

Executing the oozie cooridnator job
------------------------------------
Step 1) Modify coordinator.properties file
Set the start and end time to be in the future, UTC, so you can see how the job is in waiting state prior to start time condition being met; The following are the entries that need to be changed.
startTime=2013-07-09T03:45Z
endTime=2013-07-09T03:47Z
Step 2) Submit the coordinator job
$ oozie job -oozie http://cdh-dev01:11000/oozie -config oozieProject/sampleCoordinatorJobTrigFileDep/coordinatorConf/coordinator.properties -run
 
Step 3) Publish trigger file to run job
 
$ hadoop fs -put oozieProject/sampleCoordinatorJobTrigFileDep/triggerDir oozieProject/sampleCoordinatorJobTrigFileDep
 
 
Replace cdh-dev01 with your oozie server, and 11000 with the associated port number;

utput of java program:
------------------------
$ hadoop fs -ls -R oozieProject/sampleCoordinatorJobTrigFileDep/out*/part* | awk '{print $8}' | xargs hadoop fs -cat
2013-NetworkManager 7
2013-console-kit-daemon 7
2013-gnome-session 11
2013-init 166
2013-kernel 810
2013-login 2
2013-nm-dispatcher.action 4
2013-ntpd_initres 4133
2013-polkit-agent-helper-1 8
2013-pulseaudio 18
2013-spice-vdagent 15
2013-sshd 6
2013-sudo 8
2013-udevd 6


Results of report 2, from execution of hiveConsolidated-Year-Month-Report.hql
------------------------------------------------------------------------------
hive>
set hive.cli.print.header=true;
hive> select * from consolidated_YM_report;
OK
process node year month occurrence
NULL cdh-dev01 2013 5 19
NULL cdh-vms 2013 5 6
NetworkManager cdh-dev01 2013 5 7
console-kit-daemon cdh-dev01 2013 5 7
gnome-session cdh-dev01 2013 5 11
init cdh-dev01 2013 5 38
init cdh-dn01 2013 5 17
init cdh-dn02 2013 5 17
init cdh-dn03 2013 5 23
init cdh-jt01 2013 5 17
init cdh-nn01 2013 5 29
init cdh-vms 2013 5 25
kernel cdh-dev01 2013 5 203
kernel cdh-dn01 2013 5 67
kernel cdh-dn02 2013 5 58
kernel cdh-dn03 2013 5 58
kernel cdh-jt01 2013 5 76
kernel cdh-nn01 2013 5 172
kernel cdh-vms 2013 5 176
login cdh-vms 2013 5 2
nm-dispatcher.action cdh-dev01 2013 5 4
ntpd_initres cdh-dev01 2013 5 57
ntpd_initres cdh-dn01 2013 5 803
ntpd_initres cdh-dn02 2013 5 804
ntpd_initres cdh-dn03 2013 5 792
ntpd_initres cdh-jt01 2013 5 804
ntpd_initres cdh-nn01 2013 5 834
ntpd_initres cdh-vms 2013 5 39
polkit-agent-helper-1 cdh-dev01 2013 5 8
pulseaudio cdh-dev01 2013 4 1
pulseaudio cdh-dev01 2013 5 17
spice-vdagent cdh-dev01 2013 4 1
spice-vdagent cdh-dev01 2013 5 14
sshd cdh-dev01 2013 5 6
sudo cdh-dn02 2013 4 1
sudo cdh-dn02 2013 5 1
sudo cdh-dn03 2013 4 1
sudo cdh-dn03 2013 5 1
sudo cdh-jt01 2013 4 3
sudo cdh-jt01 2013 5 1
udevd cdh-dn01 2013 5 1
udevd cdh-dn02 2013 5 1
udevd cdh-dn03 2013 5 1
udevd cdh-jt01 2013 5 1
udevd cdh-vms 2013 5 2
Time taken: 5.841 seconds


Results of report 3, from execution of hiveTop3Processes-Year-Report.hql
------------------------------------------------------------------------
--Get top3 issues logged by year
hive>
set hive.cli.print.header=true;
hive>
select * from top3_process_by_year_report;
process year occurrence
ntpd_initres 2013 4133
kernel 2013 810
init 2013 166
Time taken: 0.385 seconds
