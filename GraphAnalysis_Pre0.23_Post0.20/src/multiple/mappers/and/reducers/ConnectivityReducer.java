package multiple.mappers.and.reducers;

import java.io.IOException;

import org.apache.hadoop.io.Text;

/**
 * 	  
 * Description : Reducer class that implements the reduce part of checking for connectivity of a graph.  It extends the SearchReducer class.
 * It calls the super class' reduce method and increments the counter if the color of the returned node is WHITE indicating that the graph is not connected.
 * 
 * Input format <key, value> : <nodeID,  list_of_adjacent_nodes|distance_from_the_source|color|parent_node>
 * 
 * Output format <key, value> : <nodeID, (updated) list_of_adjacent_nodes|distance_from_the_source|color|parent_node>
 * 
 * Reference : http://www.johnandcailin.com/blog/cailin/breadth-first-graph-search-using-iterative-map-reduce-algorithm
 * 
 *         
 */

// the type parameters are the input keys type, the input values type, the
// output keys type, the output values type

public class ConnectivityReducer extends SearchReducer{


	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

		//initialize the lightestColor to be BLACK, the lightestColor will be updated if the color of the incoming node is
		//lighter than the color of the current node
		Color lightestColor = Color.BLACK;
		
		//create a new out node and set its values
		Node outNode = new Node();
		//call the reduce method of SearchReducer class 
		outNode = super.reduce(key, values, context, outNode);
		
		// save the lightest color which is useful to find if the graph
        // is connected or not
        if (outNode.getColor().ordinal() < lightestColor.ordinal()) {

            lightestColor = outNode.getColor();
        }
        
		long prevCntrValue = context.getCounter(
				EulerCounters.numberOfIterations).getValue();
		// if the color of the node is gray, the execution has to continue
		if (outNode.getColor()== Color.GRAY) {
			context.getCounter(EulerCounters.numberOfIterations).increment(1L);

		}
		long currCntrValue = context.getCounter(
				EulerCounters.numberOfIterations).getValue();

		// to determine if there are any more gray nodes, we check if the
		// prevCntrValue == currCntrVale
		// if there are no more gray nodes and the lightest color is WHITE
		// then the graph is disconnected
		// so increment the NotConnected Counter
		if (prevCntrValue == currCntrValue 	&& lightestColor == Color.WHITE) {

			context.getCounter(EulerCounters.isNotConnected).increment(1L);

		}

	}
}