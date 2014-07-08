import java.io.*;
import java.util.*;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

public class MyReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
	public void reduce(Text key, Iterator<Text> values,OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		while (values.hasNext()) {
			output.collect(key, values.next());
		}
	}
}