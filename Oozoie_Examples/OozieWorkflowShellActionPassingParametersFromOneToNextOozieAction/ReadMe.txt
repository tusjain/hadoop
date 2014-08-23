oozie workflow that demonstrates capturing output from one action to another.  
The application includes a shell script action that outputs the count of the number of 
lines in a file glob, and an email action that captures the output of the shell action 
and emails it.

This example includes components of a oozie workflow - scripts/code, sample data
and commands; Oozie actions covered: shell action, email action
 
Action 1: The shell action executes a shell script that does a line count for files in a
glob provided, and writes the line count to standard output
Action 2: The email action emails the output of action 1

Includes:
---------
Data and script download: 01-DataAndScriptDownload
Data load commands: 02-HdfsLoadCommands
Shell Script: 03-ShellScript
Oozie job properties file: 04-OozieJobProperties
Oozie workflow file: 05-OozieWorkflowXML
Oozie SMTP Configuration: 06-OozieSMTPConfig
Oozie commands 07-OozieJobExecutionCommands
Output email 08-OutputOfProgram

Directory structure
-------------------
oozieProject
	data
		kashit-syslog
			<<Node-Name>>
				<<Year>>
					<<Month>>
						messages
	workflowShellAction
		workflow.xml
		job.properties
		lineCount.sh

02-Hdfs load commands
----------------------
$ hadoop fs -mkdir oozieProject
$ hadoop fs -put oozieProject/* oozieProject/

Oozie SMTP configuration
------------------------
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


06. Oozie commands
-------------------
Note: Replace oozie server and port, with your cluster-specific.
1) Submit job:
$ oozie job -oozie http://cdh-dev01:11000/oozie -config oozieProject/workflowShellAction/job.properties -submit
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
$ oozie job -oozie http://cdh-dev01:11000/oozie -config oozieProject/workflowShellAction/job.properties -rerun 0000014-130712212133144-oozie-oozi-W
7) Should you need to kill the job:
$ oozie job -oozie http://cdh-dev01:11000/oozie -kill 0000014-130712212133144-oozie-oozi-W
8) View server logs:
$ oozie job -oozie http://cdh-dev01:11000/oozie -logs 0000014-130712212133144-oozie-oozi-W
Logs are available at:
/var/log/oozie on the Oozie server.


########################
#Program output
########################
 
From akhanolk@cdh-dev01.localdomain Thu Sep 12 00:51:00 2013
Return-Path: <akhanolk@cdh-dev01.localdomain>
X-Original-To: akhanolk@cdh-dev01
Delivered-To: akhanolk@cdh-dev01.localdomain
From: akhanolk@cdh-dev01.localdomain
To: akhanolk@cdh-dev01.localdomain
Subject: Output of workflow 0000009-130911235633916-oozie-oozi-W
Content-Type: text/plain; charset=us-ascii
Date: Thu, 12 Sep 2013 00:51:00 -0500 (CDT)
Status: R
 
Results from line count: 5207

