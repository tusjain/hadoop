import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

//WordCountReducer - is doing nothing special just writing the key in the context
 public  class WordCountReducer extends MapReduceBase implements Reducer<Text, IntWritable,Text, IntWritable> {

    public void reduce(Text key, Iterator values,OutputCollector output, Reporter reporter) throws IOException {
        int sum = 0;
        output.collect(key, new IntWritable(sum));
    }
}