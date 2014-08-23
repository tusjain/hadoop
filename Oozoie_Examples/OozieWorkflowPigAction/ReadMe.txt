This example includes oozie workflow components to run a pig latin script to parse
(Syslog generated) log files using regex;
Usecase: Count the number of occurances of processes that got logged, by month,
day and process.
 

Includes:
---------
Sample data and structure: 01-SampleDataAndStructure
Data and script download: 02-DataAndScriptDownload
Data load commands: 03-HdfsLoadCommands
Pig script: 04-PigLatinScript
Pig script execution command: 05-PigLatinScriptExecution
Oozie job properties: 06-JobProperties
Oozie workflow: 07-OozieWorkflowXML
Oozie job exection command: 08-OozieCommand
Oozie job output 09-Output

1a. Sample data
----------------
May 3 11:52:54 cdh-dn03 init: tty (/dev/tty6) main process (1208) killed by TERM signal
May 3 11:53:31 cdh-dn03 kernel: registered taskstats version 1
May 3 11:53:31 cdh-dn03 kernel: sr0: scsi3-mmc drive: 32x/32x xa/form2 tray
May 3 11:53:31 cdh-dn03 kernel: piix4_smbus 0000:00:07.0: SMBus base address uninitialized - upgrade BIOS or use force_addr=0xaddr
May 3 11:53:31 cdh-dn03 kernel: nf_conntrack version 0.5.0 (7972 buckets, 31888 max)
May 3 11:53:57 cdh-dn03 kernel: hrtimer: interrupt took 11250457 ns
May 3 11:53:59 cdh-dn03 ntpd_initres[1705]: host name not found: 0.rhel.pool.ntp.org
1b. Structure
--------------
Month = May
Day = 3
Time = 11:52:54
Node = cdh-dn03
Process = init:
Log msg = tty (/dev/tty6) main process (1208) killed by TERM signal


2b. Directory structure applicable for this blog/gist:
-------------------------------------------------------
oozieProject
	data
		kashit-syslog
			<<Node-Name>>
				<<Year>>
					<<Month>>
						messages
 
	workflowPigAction
		workflow.xml
		job.properties
		reportScript.pig
		
3. Data load commands
----------------------
Save the zip file to your home directory and unzip.
 
$ hadoop fs -mkdir oozieProject
$ hadoop fs -put oozieProject/* oozieProject/
 
Validate load:
 
$ hadoop fs -ls -R oozieProject/workflowPigAction | awk '{print $8}'
 
Should match listing in 2b, above

08. Oozie commands
-------------------
Note: Replace oozie server and port, with your cluster-specific.
1) Submit job:
$ oozie job -oozie http://cdh-dev01:11000/oozie -config oozieProject/workflowPigAction/job.properties -submit
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
$ oozie job -oozie http://cdh-dev01:11000/oozie -config oozieProject/workflowPigAction/job.properties -rerun 0000014-130712212133144-oozie-oozi-W
7) Should you need to kill the job:
$ oozie job -oozie http://cdh-dev01:11000/oozie -kill 0000014-130712212133144-oozie-oozi-W
8) View server logs:
$ oozie job -oozie http://cdh-dev01:11000/oozie -logs 0000014-130712212133144-oozie-oozi-W
Logs are available at:
/var/log/oozie on the Oozie server.

Program output
--------------
 
$ hadoop fs -cat oozieProject/workflowPigAction/output/SortedResults/part-r-00000
 
Apr pulseaudio[5705]: 1
Apr spice-vdagent[5657]: 1
Apr sudo: 5
May NetworkManager[1232]: 1
May NetworkManager[1243]: 1
May NetworkManager[1284]: 1
May NetworkManager[1292]: 1
.........

