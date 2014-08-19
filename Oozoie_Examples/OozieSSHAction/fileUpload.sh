#################################
# Name: uploadFile.sh
# Location: remote node where we
# want to run an
# operation
#################################
#!/bin/bash
 
hadoop fs -rm -R oozieProject/results-sshAction/*
hadoop fs -put ~/data/* oozieProject/results-sshAction/
 
status=$?
 
if [ $status = 0 ]; then
echo "STATUS=SUCCESS"
else
echo "STATUS=FAIL"
fi