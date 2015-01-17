package custom.combiner;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

	//the type parameters are the input keys type, the input values type, the output keys type, the output values type

	// The reducer outputs the key, value pair where the key is the outer vertices of the triad associated with it
	// for example, if the input to the reducer is <2, {1,2}> and <2,{2,3}>,
	// then the following reducer will output < {1,3},<{1,2},{2,3}>
	// the output of this reducer can be used as a partial input to the mapper and the reducer to enumerate triangles
	public class TriadReducer extends Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			String concatVal = "";
			Set<String> nodeSet = new HashSet<String>();
			for (Text val : values) {
				String[] valTokens = val.toString().split(",");
				for (int i = 0; i < valTokens.length; i++) {
					nodeSet.add(valTokens[i]);
				}
				concatVal += "|" + val;
			}
			nodeSet.remove(key.toString());
			String nodesKey = " ";
			for (String node : nodeSet) {
				nodesKey += node + ",";
			}
			context.write(new Text(nodesKey), new Text(concatVal));
		}
	}