package user.defined.configuration.parameters;

import java.io.IOException;

import org.apache.hadoop.io.Text;

/**
 * 
 * @author Deepika Mohan
 * 
 *         Description : Mapper class that implements the map part of  Breadth-first search
 *         algorithm as used in the map part of the reachability program. It extends the SearchMapper class.
 
 *         Input format : nodeID<tab>list_of_adjacent_nodes|distance_from_the_source|color|parent_node
 * 
 *         Reference :
 *         http://www.johnandcailin.com/blog/cailin/breadth-first-graph
 *         -search-using-iterative-map-reduce-algorithm
 * 
 *         Hadoop version used : 0.20.2
 */

// the type parameters are the input keys type, the input values type, the
// output keys type, the output values type
public class ReachabilityMapper extends SearchMapper {

	
	@Override
	public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {

		Node inNode = new Node(value.toString());

		String src = context.getConfiguration().get("source"); //get the source id
		
		//the source id is passed as a command-line argument, we set the color of the source as GRAY, the distance from the source as 0 
		if(inNode.getId().equals(src)){
			//updating the fields of the source node
			inNode.setColor(Color.GRAY);
			inNode.setDistance(0);
			inNode.setParent("source");
		}
		
		//call the super class' map method passing the inNode as a parameter
		super.map(key, value, context, inNode);
	}
}
