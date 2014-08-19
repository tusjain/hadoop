Oozie server is running on node cdh-dev01 in my environment.
With the sample workflow application, I am going to submit an Oozie 
job while logged in as myself (akhanolk), on this machine (Oozie server - cdh-dev01)
 from the CLI.
The workflow executes a shell script on cdh-dn01 as user akhanolk.  
The shell script loads a local file to HDFS.  If the file load completes successfully, 
the workflow sends an email to me.

SSH setup:
1. Passphrase-less SSH for akhanolk from cdh-dev01 (Oozie server) to cdh-dn01 (remote node) and vice versa
2.  Passphrase-less SSH for oozie user ID (oozie in my case) on cdh-dev01 to cdh-dn01 as akhanolk
[Running ps -ef | grep oozie on Oozie server will give you the configured Oozie user ID]


Workflow application components:
workflow definition (workflow.xml - in HDFS)
job properties file (job.properties from node submitting job)
Shell script (uploadFile.sh) on remote node (cdh-dn01; At /home/akhanolk/scripts)
Data file (employees_data) on remote node (cdh-dn01; At /home/akhanolk/data)

Desired result:
Upon execution of the workflow, the employees_data on cdh-dn01 should get moved to a specified directory in HDFS


Directory structure of application download
--------------------------------------------
oozieProject
	workflowSshAction
		job.properties
		workflow.xml
	scripts
		uploadFile.sh
	data
		employees_data
		

*****************************************
Location of files/scripts & commands
*****************************************
 
I have pasted information specific to my environment; Modify as required.
 
1) Node (cdh-dev01) where the Oozie CLI will be used to submit/run Oozie workflow:
 
Structure/Path:
~/oozieProject/workflowSshAction/job.properties
 
2) HDFS:
 
Workflow directory structure:
/user/akhanolk/oozieProject/workflowSshAction/workflow.xml
 
Commands to load:
hadoop fs -mkdir oozieProject
hadoop fs -mkdir oozieProject/workflowSshAction
hadoop fs -put ~/oozieProject/workflowSshAction/workflow.xml oozieProject/workflowSshAction
 
Output directory structure:
/user/akhanolk/oozieProject/results-sshAction
 
Command:
hadoop fs -mkdir oozieProject/results-sshAction
 
3) Remote node (cdh-dn01) where we want to run a shell script:
 
Directory structure/Path:
~/scripts/uploadFile.sh
~/data/employee_data

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

************************
SSH setup
************************
Issues:
Review my section on issues encountered to see all the issues and fixes I had to make
to get the workflow application to work.
 
------------------------------------------------------------------------------------------------------
Oozie documentation:
To run SSH Testcases and for easier Hadoop start/stop configure SSH to localhost to be passphrase-less.
Create your SSH keys without a passphrase and add the public key to the authorized file:
 
$ ssh-keygen -t dsa
$ cat ~/.ssh/id_dsa.pub >> ~/.ssh/authorized_keys2
Test that you can ssh without password:
 
$ ssh localhost
------------------------------------------------------------------------------------------------------
SSH tutorial:
Setup ssh - https://www.digitalocean.com/community/articles/how-to-set-up-ssh-keys--2

Oozie commands
---------------
Note: Replace oozie server and port, with your cluster-specific.
1) Submit job:
$ oozie job -oozie http://cdh-dev01:11000/oozie -config oozieProject/workflowSshAction/job.properties -submit
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
$ oozie job -oozie http://cdh-dev01:11000/oozie -config oozieProject/workflowSshAction/job.properties -rerun 0000014-130712212133144-oozie-oozi-W
7) Should you need to kill the job:
$ oozie job -oozie http://cdh-dev01:11000/oozie -kill 0000014-130712212133144-oozie-oozi-W
8) View server logs:
$ oozie job -oozie http://cdh-dev01:11000/oozie -logs 0000014-130712212133144-oozie-oozi-W
Logs are available at:
/var/log/oozie on the Oozie server.


************************
Output
************************
 
[akhanolk@cdh-dev01 ~]$ hadoop fs -ls oozieProject/res*
Found 1 items
-rw-r--r-- 3 akhanolk akhanolk 13821993 2013-10-30 20:59 oozieProject/results-sshAction/employees_data


********************
Output email
********************
From akhanolk@cdh-dev01.localdomain Wed Oct 30 22:59:16 2013
Return-Path: <akhanolk@cdh-dev01.localdomain>
X-Original-To: akhanolk@cdh-dev01
Delivered-To: akhanolk@cdh-dev01.localdomain
From: akhanolk@cdh-dev01.localdomain
To: akhanolk@cdh-dev01.localdomain
Subject: Output of workflow 0000003-131029234028597-oozie-oozi-W
Content-Type: text/plain; charset=us-ascii
Date: Wed, 30 Oct 2013 22:59:16 -0500 (CDT)
Status: R
 
Status of the file move: SUCCESS

