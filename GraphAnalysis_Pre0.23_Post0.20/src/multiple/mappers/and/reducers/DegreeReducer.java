package multiple.mappers.and.reducers;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import HadoopGTK.Node;

/**
 *  Description : Reducer class that checks if all the nodes are of even degree and increments the counter value even if one node is not of
 *  even degree. The process of checking does not terminate when a node of odd degree is found. The process continues until the degree of all the nodes are checked.
 *   
 *  Input format <key, value> : <nodeID,  list_of_adjacent_nodes|distance_from_the_source|color|parent>
 *  
 *  Output format <key, value> : <nodeID, (updated) list_of_adjacent_nodes|distance_from_the_source|color|parent>
 *   
 *         
 */


public class DegreeReducer extends Reducer<Text, Text, Text, Text> {

	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

		for (Text value : values) {

			Node inNode = new Node(key.toString() + "\t" + value.toString());
			int degree = inNode.getEdges().size(); // get the degree of the node

			if (degree % 2 != 0) { // if there is at least one node whose
				// degree is odd, set the variable
				// isEvenDegree to false and break

				context.getCounter(EulerCounters.isNotEvenDegree).increment(1L);
				
			}

			context.write(key, value);
		}

	}
}