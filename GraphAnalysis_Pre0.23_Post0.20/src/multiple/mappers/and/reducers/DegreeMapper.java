package multiple.mappers.and.reducers;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Description : Mapper class that prepares the data suitable for the DegreeReducer that checks if all the nodes are of even degree.
 * 
 * Input format <key, value>  : nodeID<tab>list_of_adjacent_nodes|distance_from_the_source|color|parent
 * 
 * Output format <key, value> : < nodeID, (updated) list_of_adjacent_nodes|distance_from_the_source|color|parent>
 *  
 *         
 */

// the type parameters are the input keys type, the input values type, the
// output keys type, the output values type
public class DegreeMapper extends Mapper<Object, Text, Text, Text> {

	
	public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {

		Node node = new Node(value.toString());
		context.write(new Text(node.getId()), node.getNodeInfo());

	}
}