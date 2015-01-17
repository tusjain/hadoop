package find.intersection.of.edges.among.multiple.graphs;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Reducer class for the intersection problem. The intermediate keys and values from the IntersectMapper form the input to the IntersectReducer.
 * The output of the IntersectReducer will be the common edge found along with the graph ids.
 * 
 * Input format : <source: destination, graphId>
 * Output format :<source: destination, graphId>
 **/
public class IntersectReducer extends Reducer<Text, Text, Text, Text> {
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		boolean first = true;//used for formatting the output
		StringBuilder toReturn = new StringBuilder();
		Set<String> graphIds = new HashSet<String>();
		Iterator<String> graphIdItr;
		int numberOfGraphs = 3; // the number of graphs is set as 3, it can be changed according to the number of graphs used in the program

		// add the graph ids to the set. This is to eliminate considering
		// multiple edges between same pair of nodes in same graphk
		for (Text val : values) {
			graphIds.add(val.toString());
		}
		graphIdItr = graphIds.iterator();

		// Iterate through the graphs and append the graph ids to a stringbuilder
		while (graphIdItr.hasNext()) {
			//for better formatting of the output
			if (!first){
				toReturn.append('^');
			}
			first = false;
			toReturn.append(graphIdItr.next());
		}
		String intersect = new String(toReturn);
		StringTokenizer st = new StringTokenizer(intersect, "^");

		// use StringTokenizer and if the number of graph ids is equal to the number of graphs we're considering, write to the context
		if (st.countTokens() == numberOfGraphs){
			
			//emit the key, value pair from the reducer
			//here the key is the source:destination pair and the value will be the concatenated graph ids that has this source:destination pair
			context.write(key, new Text(intersect));
		}
	}
}