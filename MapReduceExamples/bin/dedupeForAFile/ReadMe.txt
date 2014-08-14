How to get distinct values/lines (dedupe) for a file using Hadoop Map Reduce Framework

Compile, create Jar and Run
 
javac -classpath hadoop-0.20.1-dev-core.jar -d CalculateDistinct/ CalculateDistinct.java
jar -cvf CalculateDistinct.jar -C CalculateDistinct/ .
hadoop jar CalculateDistinct.jar org.myorg.CalculateDistinct /user/john/in/abc.txt /user/john/out