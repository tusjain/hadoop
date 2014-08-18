import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

//LastMapper - will spilt the record sent from reducer and write into the final output file
 public class LastMapper extends MapReduceBase implements Mapper<Text, IntWritable,Text, IntWritable> {
   
    public void map(Text key, IntWritable value,OutputCollector output,Reporter reporter) throws IOException {
        String[] word = key.toString().split(",");
        System.out.println("Upper Case:"+word);
        output.collect(new Text(word[0]), new Text(word[1]));   
    }
}