package multiple.mappers.and.reducers;

import java.io.IOException;

import org.apache.hadoop.io.Text;

import HadoopGTK.Node;
import HadoopGTK.SearchMapper;

/**
 * 
 * Description : Mapper class that implements the map part of checking for connectivity of a graph. It extends the SearchMapper class and calls the super class' map method..
 * Two nodes are said to be connected if there is a path between the two nodes in the graph.
 *       
 * Reference : http://www.johnandcailin.com/blog/cailin/breadth-first-graph-search-using-iterative-map-reduce-algorithm
 * 
 *      
 */
public class ConnectivityMapper extends SearchMapper {

	
	public void map(Object key, Text value, Context context)
	throws IOException, InterruptedException {
	
		Node inNode = new Node(value.toString());
		super.map(key, value, context, inNode);

	}
}