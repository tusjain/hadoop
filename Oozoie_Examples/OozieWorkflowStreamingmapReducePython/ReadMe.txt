This gist includes oozie workflow components (streaming map reduce action) to execute
python mapper and reducer scripts to parse Syslog generated log files using regex;
Usecase: Count the number of occurances of processes that got logged, by month, and process.
 
Includes:
---------
Sample data and structure: 01-SampleDataAndStructure
Data and script download: 02-DataAndScriptDownload
Data load commands: 03-HdfsLoadCommands
Python mapper script: 04A-PythonMapperScript
Python reducer script: 04B-PythonReducerScript
Python script test commands: 05-PythonScriptTest
Oozie job properties: 06-JobProperties
Oozie workflow: 07-OozieWorkflowXML
Oozie job exection command: 08-OozieCommands
Oozie job output 09-Output


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



Directory structure applicable for this blog:
---------------------------------------------
oozieProject
data
kashit-syslog
<<node>>
<<year>>
<<month>>
messages
workflowStreamingMRActionPy
workflow.xml
job.properties
LogParserMapper.py
LogParserReducer.py

Load downloaded files to HDFS
------------------------------
--Create project directory
$ hadoop fs -mkdir oozieProject
 
--Deploy data and workflow
$ hadoop fs -put oozieProject/* oozieProject/
 
Directory structure on HDFS
----------------------------
 
$ hadoop fs -ls -R oozieProject/workflowStreamingMRActionPy | awk '{print $8}'


#---------------------------------------------
# Testing the python scripts outside of oozie
#---------------------------------------------
 
#Test the mapper from the directory where the data is located:
cat oozieProject/data/*/*/*/*/* | python oozieProject/workflowStreamingMRActionPy/LogParserMapper.py
 
#Test mapper and reducer
cat oozieProject/data/*/*/*/*/* | python oozieProject/workflowStreamingMRActionPy/LogParserMapper.py | sort | python oozieProject/workflowStreamingMRActionPy/LogParserReducer.py | sort
 
#Delete prior copy of scripts
hadoop fs -rm -R oozieProject/workflowStreamingMRActionPy/
 
 
#Load application, if not already done..
hadoop fs -put ~/oozieProject/workflowStreamingMRActionPy/ oozieProject/
 
#Run on cluster (update paths as needed)
hadoop jar /opt/cloudera/parcels/CDH-4.2.0-1.cdh4.2.0.p0.10/lib/hadoop-0.20-mapreduce/contrib/streaming/hadoop-streaming-2.0.0-mr1-cdh4.2.0.jar -jobconf mapred.reduce.tasks=1 -file oozieProject/workflowStreamingMRActionPy/LogParserMapper.py -mapper oozieProject/workflowStreamingMRActionPy/LogParserMapper.py -file oozieProject/workflowStreamingMRActionPy/LogParserReducer.py -reducer oozieProject/workflowStreamingMRActionPy/LogParserReducer.py -input oozieProject/data/*/*/*/*/* -output oozieProject/workflowStreamingMRActionPy/output-streaming-manualRun
 
#View output
$ hadoop fs -ls -R oozieProject/workflowStreamingMRActionPy/output-streaming-manualRun/part* | awk '{print $8}' | xargs hadoop fs -cat
May-spice-vdagent[2020]: 1
May-ntpd_initres[997]: 3
May-nm-dispatcher.action: 4
May-NetworkManager[1232]: 1
May-init: 166
............


# -------------------------------------------------
# This is the job properties file - job.properties
# -------------------------------------------------
 
# Replace name node and job tracker information with that specific to your cluster
 
nameNode=hdfs://cdh-nn01.hadoop.com:8020
jobTracker=cdh-jt01:8021
queueName=default
 
oozie.libpath=${nameNode}/user/oozie/share/lib
oozie.use.system.libpath=true
oozie.wf.rerun.failnodes=true
 
oozieProjectRoot=${nameNode}/user/${user.name}/oozieProject
appPath=${oozieProjectRoot}/workflowStreamingMRActionPy
oozie.wf.application.path=${appPath}
oozieLibPath=${oozie.libpath}
 
inputDir=${oozieProjectRoot}/data/*/*/*/*/*
outputDir=${appPath}/output


08. Oozie commands
-------------------
Note: Replace oozie server and port, with your cluster-specific.
1) Submit job:
$ oozie job -oozie http://cdh-dev01:11000/oozie -config oozieProject/workflowStreamingMRActionPy/job.properties -submit
job: 0000017-130712212133144-oozie-oozi-W
2) Run job:
$ oozie job -oozie http://cdh-dev01:11000/oozie -start 0000017-130712212133144-oozie-oozi-W
3) Check the status:
$ oozie job -oozie http://cdh-dev01:11000/oozie -info 0000017-130712212133144-oozie-oozi-W
4) Suspend workflow:
$ oozie job -oozie http://cdh-dev01:11000/oozie -suspend 0000017-130712212133144-oozie-oozi-W
5) Resume workflow:
$ oozie job -oozie http://cdh-dev01:11000/oozie -resume 0000017-130712212133144-oozie-oozi-W
6) Re-run workflow:
$ oozie job -oozie http://cdh-dev01:11000/oozie -config oozieProject/workflowSqoopAction/job.properties -rerun 0000017-130712212133144-oozie-oozi-W
7) Should you need to kill the job:
$ oozie job -oozie http://cdh-dev01:11000/oozie -kill 0000017-130712212133144-oozie-oozi-W
8) View server logs:
$ oozie job -oozie http://cdh-dev01:11000/oozie -logs 0000017-130712212133144-oozie-oozi-W
Logs are available at:
/var/log/oozie on the Oozie server.


Output
-------
 
$ hadoop fs -ls -R oozieProject/workflowStreamingMRActionPy/output/part-* | awk '{print $8}' | xargs hadoop fs -cat
 
May-gnome-session[2010]: 3
May-ntpd_initres[1872]: 24
May-ntpd_initres[1705]: 792
May-spice-vdagent[2114]: 1
May-pulseaudio[2135]: 1
May-pulseaudio[2076]: 1
May-spice-vdagent[1974]: 1
May-ntpd_initres[1720]: 792
May-ntpd_initres[1084]: 6
May-spice-vdagent[2109]: 1
May-pulseaudio[2257]: 1
May-NetworkManager[1292]: 1
May-pulseaudio[2032]: 1
May-kernel: 810
May-ntpd_initres[1592]: 798
May-NetworkManager[1342]: 1
May-polkit-agent-helper-1[2036]: 8
May-spice-vdagent[1955]: 1
May-console-kit-daemon[1779]: 4
May-spice-vdagent[2016]: 1
May-ntpd_initres[1026]: 9
May-pulseaudio[2039]: 1
May-gnome-session[2009]:
...........
