import java.io.IOException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Reducer;

public class SalaryCalculatorReducer extends Reducer<Text, Text, Text, Text>{
      //Reduce method for just outputting the key from mapper as the value from mapper is just an empty string    
      public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
             context.write(key, new Text(""));
           }
}