package user.defined.configuration.parameters;

import java.io.IOException;

import org.apache.hadoop.io.Text;

/**
 * 
 * @author Deepika Mohan
 * 
 *         Description : Reducer class that implements the reduce part of  Reachability 
 *         algorithm. This reducer class extends the SearchReducer class. If the destination is found in the list of adjacent nodes during the BFS, then the counter is incremented
 * 
 *         Input format : <nodeID, list_of_adjacent_nodes|distance_from_the_source|color|parent>
		   Output format : <nodeID, (updated)    list_of_adjacent_nodes|distance_from_the_source|color|parent>
 * 
 *         Reference :
 *         http://www.johnandcailin.com/blog/cailin/breadth-first-graph
 *         -search-using-iterative-map-reduce-algorithm
 * 
 *         Hadoop version used : 0.20.2
 */
public class ReachabilityReducer extends SearchReducer {

	@Override
	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

		//create a new out node and set its values
		Node outNode = new Node();
		
		//call the reduce method of SearchReducer class 
		outNode = super.reduce(key, values, context, outNode);				
		
		String dest = context.getConfiguration().get("destination"); //get the destination node

		//if the color of the node is not WHITE and if the destination is processed, then note that the destination is found by incrementing the counter
		if(outNode.getColor() != Color.WHITE && key.toString().equals(dest)){
			
			context.getCounter(ReachabilityCounters.isDestinationFound).increment(1L);
		}

		//if the color of the node is gray, the execution has to continue, this is done by incrementing the counter
		if (outNode.getColor() == Color.GRAY){
			context.getCounter(ReachabilityCounters.numberOfIterations).increment(1L);
		}
	}
}
