How to count number of lines in a file using Map Reduce framework

Compile, Create Jar and Run
 
javac -classpath hadoop-0.20.1-dev-core.jar -d LineCount/ LineCount.java
jar -cvf LineCount.jar -C LineCount/ .
hadoop jar LineCount.jar org.myorg.LineCount in out