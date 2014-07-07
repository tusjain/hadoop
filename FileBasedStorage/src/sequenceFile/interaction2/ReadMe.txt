This example demonstrates how to create a sequence file (compressed and uncompressed), from a text file.

1. Input data and script download
1. Input data: DataFule.txt  --> move this file to input folder to run this example
2. Command to run Java program

hadoop jar formatConverterTextToSequence.jar FormatConverterTextToSequenceDriver input


3. Command to run program to convert sequense file into text file

$ hadoop jar FormatConverterSequenceToText.jar FormatConverterSequenceToTextDriver input
 
$ hadoop fs -ls -R input | awk '{print $8}'

4. Command to run program for compression

$ hadoop jar formatProject/formatConverterTextToSequence/jars/formatConverterTextToBlkCompSequence.jar FormatConverterTextToBlckCompSequenceDriver formatProject/data/departments_sorted/part-m-00000 formatProject/data/departments_sequence_blckcmp
.
 
$ hadoop fs -ls -R input | awk '{print $8}'