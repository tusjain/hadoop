How to count number of times a word appeared in a file using Map Reduce framework

Compile, create jar and run
javac -classpath hadoop-0.20.1-dev-core.jar -d WordCount/ WordCount.java
jar -cvf WordCount.jar -C WordCount/ .
hadoop jar WordCount.jar org.myorg.WordCount in out