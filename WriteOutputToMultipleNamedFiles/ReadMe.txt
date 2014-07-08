This project will write output of reducer to multiple named files.

Suppose you have input file that contains the following data
Name:Gita
Age:24
Name:Yash
Age:16
 
This can be done by using MultipleTextOutputFormat class. 

The example is an implementation of MultipleTextOutputFormat class which will read the data file
and create 2 output files Name and Age.

Class MultiFileOutput  extends MultipleTextOutputFormat. 
What this means is that when the reducer is ready to spit out the Key/Value pair then before writing 
it to a file, it passes them to method generateFileNameForKeyValue. The logic to name the output 
file is the embedded in this method (in this case the logic is to create 1 file per key). 
The String returned by method generateFileNameForKeyValue determines the name of the file where this
Key/Value pair is logged. 
 