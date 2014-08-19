This example includes components of a simple workflow application (oozie 3.3.0) that generates
a report off of data in a Hive table; Emails are sent out to notify designated users of
success/failure of workflow. The data is syslog generated log files; The Hive table row
format serde is regex serde.

The sample application includes:
--------------------------------
1. Oozie actions: hive action and email action
2. Oozie workflow controls: start, end, and kill.
3. Workflow components: job.properties and workflow.xml
4. Sample data
5. Commands to deploy workflow, submit and run workflow

01. Workflow Components:
------------------------
1. job.properties
File containing:
a) parameter and value declarations that are referenced in the workflows, and
b) environment information referenced by Oozie to run the workflow including name node, job tracker, workflow application path etc
2. workflow.xml
Workflow definition file
 
3. hive-site.xml
Hive configuration file
 
4. Report query file in hive query language


02.1. Sample data
-------------------
May 3 11:52:54 cdh-dn03 init: tty (/dev/tty6) main process (1208) killed by TERM signal
May 3 11:53:31 cdh-dn03 kernel: registered taskstats version 1
May 3 11:53:31 cdh-dn03 kernel: sr0: scsi3-mmc drive: 32x/32x xa/form2 tray
May 3 11:53:31 cdh-dn03 kernel: piix4_smbus 0000:00:07.0: SMBus base address uninitialized - upgrade BIOS or use force_addr=0xaddr
May 3 11:53:31 cdh-dn03 kernel: nf_conntrack version 0.5.0 (7972 buckets, 31888 max)
May 3 11:53:57 cdh-dn03 kernel: hrtimer: interrupt took 11250457 ns
May 3 11:53:59 cdh-dn03 ntpd_initres[1705]: host name not found: 0.rhel.pool.ntp.org

02.2. Structure
-----------------
Month = May
Day = 3
Time = 11:52:54
Node = cdh-dn03
Process = init:
Log msg = tty (/dev/tty6) main process (1208) killed by TERM signal



03. Oozie SMTP configuration
----------------------------
Add the following to the oozie-site.xml, and restart oozie.
Replace values with the same specific to your environment.
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


06a. Hdfs commands
-------------------
 
1) Load the data and workflow application to hadoop
 
$ hadoop fs -mkdir oozieProject
$ hadoop fs -put oozieProject/* oozieProject/
 
 
2) Validate load
 
$ hadoop fs -ls -R oozieProject |awk '{print $8}'
 
..should match directory listing in section 2, above.


************************
**06b. Hive setup tasks
************************
 
a) Create table:
 
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
 
b) Create and load partitions:
 
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
 
c) Hive ql to test data loaded:
hive>
--Print headers
set hive.cli.print.header=true;
--Need to add this jar for MR to work..your env may not need it
add jar hadoop-lib/hive-contrib-0.10.0-cdh4.2.0.jar;
--Sample query
hive> select * from SysLogEvents limit 2;
OK
month_name day time host event log node year month
Apr 23 16:14:10 cdh-dev01 spice-vdagent[5657]: Missing virtio device '/dev/virtio-ports/com.redhat.spice.0': No such file or directory cdh-dev01 2013 04
Apr 23 16:14:12 cdh-dev01 pulseaudio[5705]: pid.c: Daemon already running. cdh-dev01 2013 04
Time taken: 13.241 seconds


************************************************
**06c. Hive QL - runHiveLogReport.hql
************************************************
 
use default;
drop table if exists eventsgranularreport;
 
CREATE TABLE IF NOT EXISTS eventsgranularreport(
year int,
month int,
day int,
event STRING,
occurrence int)
ROW FORMAT DELIMITED
FIELDS TERMINATED by ','
LINES TERMINATED by '\n';
 
INSERT OVERWRITE TABLE eventsgranularreport
select Year,Month,Day,Event,Count(*) Occurrence from SysLogEvents group by year,month,day,event order by event asc,year,month,day desc;

07. Oozie commands
-------------------
Note: Replace oozie server and port, with your cluster-specific.
1) Submit job:
$ oozie job -oozie http://cdh-dev01:11000/oozie -config oozieProject/workflowHiveAction/job.properties -submit
job: 0000007-130712212133144-oozie-oozi-W
2) Run job:
$ oozie job -oozie http://cdh-dev01:11000/oozie -start 0000007-130712212133144-oozie-oozi-W
3) Check the status:
$ oozie job -oozie http://cdh-dev01:11000/oozie -info 0000007-130712212133144-oozie-oozi-W
4) Suspend workflow:
$ oozie job -oozie http://cdh-dev01:11000/oozie -suspend 0000007-130712212133144-oozie-oozi-W
5) Resume workflow:
$ oozie job -oozie http://cdh-dev01:11000/oozie -resume 0000007-130712212133144-oozie-oozi-W
6) Re-run workflow:
$ oozie job -oozie http://cdh-dev01:11000/oozie -config oozieProject/workflowHiveAction/job.properties -rerun 0000007-130712212133144-oozie-oozi-W
7) Should you need to kill the job:
$ oozie job -oozie http://cdh-dev01:11000/oozie -kill 0000007-130712212133144-oozie-oozi-W
8) View server logs:
$ oozie job -oozie http://cdh-dev01:11000/oozie -logs 0000007-130712212133144-oozie-oozi-W
Logs are available at:
/var/log/oozie on the Oozie server.


08. Output
-----------
hive> select * from eventsgranularreport;
OK
year month day event occurrence
2013 5 NULL NULL 25
2013 5 3 NetworkManager[1232]: 1
2013 5 7 NetworkManager[1243]: 1
2013 5 7 NetworkManager[1284]: 1
2013 5 7 NetworkManager[1292]: 1
2013 5 7 NetworkManager[1300]: 1
2013 5 3 NetworkManager[1342]: 1
2013 5 7 NetworkManager[1422]: 1
2013 5 3 console-kit-daemon[1580]: 1
2013 5 3 console-kit-daemon[1779]: 4
2013 5 7 console-kit-daemon[1861]: 1
2013 5 7 console-kit-daemon[1964]: 1
2013 5 7 gnome-session[1902]: 1
2013 5 7 gnome-session[1980]: 3
2013 5 7 gnome-session[2009]: 3
2013 5 7 gnome-session[2010]: 3
2013 5 7 gnome-session[2033]: 1
2013 5 7 init: 104
2013 5 6 init: 12
2013 5 3 init: 50
2013 5 7 kernel: 653
2013 5 6
...








