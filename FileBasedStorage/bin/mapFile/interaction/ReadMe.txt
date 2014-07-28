This example  demonstrates how to create a map file, from a text file.

1. Input data --> DataFile.txt   To run program keep this file in input folder

2. Command to run FormatConverterTextToMap   

$ hadoop jar formatConverterTextToMap.jar FormatConverterTextToMap input output

3. Command to run program to do a lookup

hadoop jar MapFileLookup.jar MapFileLookup output d009

The key is d009 and the value is Customer Service