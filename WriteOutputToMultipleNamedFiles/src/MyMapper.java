import java.io.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

public class MyMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
	public void map(LongWritable key, Text value,OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		String [] dall=value.toString().split(":");
		output.collect(new Text(dall[0]),new Text(dall[1]));
	}
}