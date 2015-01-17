package custom.combiner;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

	//the reducer is used to aggregate the values associated with a particular key.
	// the node is the input key and the values are the edges associated with the node
	// the degree of the node is calculated by iterating through the values and
	// counting the number of edges associated with the node
	// The output of the reducer is in the form of key, value pair where the key
	// is the edge and the value is the degree of one of the ndoes in the edge
	public class AugmentDegreeReducer extends Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			ArrayList<String> list = new ArrayList<String>();
			int count = 0;
			while (values.iterator().hasNext()) {
				list.add(values.iterator().next().toString());
			}
			count = list.size();
			for (String listVal : list) {
				context.write(new Text(listVal), new Text(listVal + "|" + "degree(" + key.toString() + ")" + count + "\t"));
			}
		}
	}