package custom.combiner;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

// the second reducer in the process of augmenting the edges with the degrees.
// The edge forms the input and output keys and the degree of each the nodes in the edge forms the value

public class AugmentDegreeForEdge_Reducer extends Reducer<Text, Text, Text, Text> {
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		String concatenatedData = "";
		boolean first = true;
		for (Text val : values) {
			String[] tokens = val.toString().split("\\|");
			if (first) {
				concatenatedData += tokens[0];
				first = false;
			}
			concatenatedData += "|" + tokens[1];
		}
		context.write(key, new Text(concatenatedData));
	}
}