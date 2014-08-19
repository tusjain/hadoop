This is a simple workflow application that created a directory and moves files within
hdfs to this directory.

Emails are sent out to notify designated users of success/failure of workflow. There is a 
prepare section, to allow re-run of the action..the prepare essentially negates the move 
done by a potential prior run of the action.

The sample application includes:
--------------------------------
1. Oozie actions: hdfs action and email action
2. Oozie workflow controls: start, end, and kill.
3. Workflow components: job.properties and workflow.xml
4. Sample data
5. Commands to deploy workflow, submit and run workflow


Workflow Components:
--------------------
1. job.properties
File containing:
a) parameter and value declarations that are referenced in the workflows, and
b) environment information referenced by Oozie to run the workflow including name node, 
	job tracker, workflow application path etc
 
2. workflow.xml
Workflow definition file


Oozie SMTP configuration
------------------------
Add the following to the oozie-site.xml, and restart oozie.
Replace values with the same specific to your environment.
 
<!-- SMTP params-->
<property>
<name>oozie.email.smtp.host</name>
<value>myHost</value>
</property>
<property>
<name>oozie.email.smtp.port</name>
<value>25</value>
</property>
<property>
<name>oozie.email.from.address</name>
<value>oozie@myHost.com</value>
</property>
<property>
<name>oozie.email.smtp.auth</name>
<value>false</value>
</property>
<property>
<name>oozie.email.smtp.username</name>
<value>userName</value>
</property>
<property>
<name>oozie.email.smtp.password</name>
<value>password</value>
</property>


Commands to load data
----------------------
 
a) Load data
$ hadoop fs -mkdir oozieProject
$ hadoop fs -put oozieProject/* oozieProject/
 
b) Validate load
 
$ hadoop fs -ls -R oozieProject | awk '{print $8}'
 
You should see...
 
oozieProject/logs/kashit-syslog/<<node>>/<<year>>/<<month>>/messages
oozieProject/workflowHdfsAndEmailActions/job.properties
oozieProject/workflowHdfsAndEmailActions/workflow.xml
 
$ hadoop fs -rm -R oozieProject/data


Oozie commands
--------------
Note: Replace oozie server and port, with your cluster-specific.
 
1) Submit job:
$ oozie job -oozie http://cdh-dev01:11000/oozie -config oozieProject/workflowHdfsAndEmailActions/job.properties -submit
job: 0000001-130712212133144-oozie-oozi-W
 
2) Run job:
$ oozie job -oozie http://cdh-dev01:11000/oozie -start 0000001-130712212133144-oozie-oozi-W
 
3) Check the status:
$ oozie job -oozie http://cdh-dev01:11000/oozie -info 0000001-130712212133144-oozie-oozi-W
 
4) Suspend workflow:
$ oozie job -oozie http://cdh-dev01:11000/oozie -suspend 0000001-130712212133144-oozie-oozi-W
 
5) Resume workflow:
$ oozie job -oozie http://cdh-dev01:11000/oozie -resume 0000001-130712212133144-oozie-oozi-W
 
6) Re-run workflow:
$ oozie job -oozie http://cdh-dev01:11000/oozie -config oozieProject/workflowHdfsAndEmailActions/job.properties -rerun 0000001-130712212133144-oozie-oozi-W
 
7) Should you need to kill the job:
$ oozie job -oozie http://cdh-dev01:11000/oozie -kill 0000001-130712212133144-oozie-oozi-W
 
8) View server logs:
$ oozie job -oozie http://cdh-dev01:11000/oozie -logs 0000001-130712212133144-oozie-oozi-W
 
Logs are available at:
/var/log/oozie on the Oozie server.


Program output:
---------------
 
Expected result:
1) The data in the logs directory should be in the directory by name dataDump under oozieProject directory.
2) The directory 'logs' should be deleted.
3) An email indicating success/failure of the application
 
$ hadoop fs -ls -R oozieProject | awk '{print $8}'
 
oozieProject/dataDump/kashit-syslog
oozieProject/dataDump/kashit-syslog/cdh-dev01
oozieProject/dataDump/kashit-syslog/cdh-dev01/2013
oozieProject/dataDump/kashit-syslog/cdh-dev01/2013/04
oozieProject/dataDump/kashit-syslog/cdh-dev01/2013/04/messages
oozieProject/dataDump/kashit-syslog/cdh-dev01/2013/05
oozieProject/dataDump/kashit-syslog/cdh-dev01/2013/05/messages
oozieProject/dataDump/kashit-syslog/cdh-dn01
oozieProject/dataDump/kashit-syslog/cdh-dn01/2013
oozieProject/dataDump/kashit-syslog/cdh-dn01/2013/05
oozieProject/dataDump/kashit-syslog/cdh-dn01/2013/05/messages
oozieProject/dataDump/kashit-syslog/cdh-dn02
oozieProject/dataDump/kashit-syslog/cdh-dn02/2013
oozieProject/dataDump/kashit-syslog/cdh-dn02/2013/04
oozieProject/dataDump/kashit-syslog/cdh-dn02/2013/04/messages
oozieProject/dataDump/kashit-syslog/cdh-dn02/2013/05
oozieProject/dataDump/kashit-syslog/cdh-dn02/2013/05/messages
oozieProject/dataDump/kashit-syslog/cdh-dn03
oozieProject/dataDump/kashit-syslog/cdh-dn03/2013
oozieProject/dataDump/kashit-syslog/cdh-dn03/2013/04
oozieProject/dataDump/kashit-syslog/cdh-dn03/2013/04/messages
oozieProject/dataDump/kashit-syslog/cdh-dn03/2013/05
oozieProject/dataDump/kashit-syslog/cdh-dn03/2013/05/messages
oozieProject/dataDump/kashit-syslog/cdh-jt01
oozieProject/dataDump/kashit-syslog/cdh-jt01/2013
oozieProject/dataDump/kashit-syslog/cdh-jt01/2013/04
oozieProject/dataDump/kashit-syslog/cdh-jt01/2013/04/messages
oozieProject/dataDump/kashit-syslog/cdh-jt01/2013/05
oozieProject/dataDump/kashit-syslog/cdh-jt01/2013/05/messages
oozieProject/dataDump/kashit-syslog/cdh-nn01
oozieProject/dataDump/kashit-syslog/cdh-nn01/2013
oozieProject/dataDump/kashit-syslog/cdh-nn01/2013/05
oozieProject/dataDump/kashit-syslog/cdh-nn01/2013/05/messages
oozieProject/dataDump/kashit-syslog/cdh-vms
oozieProject/dataDump/kashit-syslog/cdh-vms/2013
oozieProject/dataDump/kashit-syslog/cdh-vms/2013/05
oozieProject/dataDump/kashit-syslog/cdh-vms/2013/05/messages
oozieProject/workflowHdfsAndEmailActions/job.properties
oozieProject/workflowHdfsAndEmailActions/workflow.xml
