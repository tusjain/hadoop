package custom.combiner;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

// The combiner is an optimization for the reducer. This combiner eliminates the nodes which are associated with at most one edge
// Thus the combiner reduces the bandwidth of data flowing to the reducer
public class TriadCombiner extends Reducer<Text, Text, Text, Text> {

	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

		// emit the keys that have a degree greater than 1
		ArrayList<String> list = new ArrayList<String>();

		int count = 0;
		while (values.iterator().hasNext()) {

			list.add(values.iterator().next().toString());

		}

		count = list.size();

		if (count > 1) {
			for (String listVal : list) {

				context.write(key, new Text(listVal));
			}

		}
	}
}