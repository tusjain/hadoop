package sorting.fearure.of.mapreduce;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


/**
 * MSTReducer is based on Kruskal's algorithm. 
 * 
 * Reference: http://www.personal.kent.edu/~rmuhamma/Algorithms/MyAlgorithms/GraphAlgor/kruskalAlgor.htm
 * 
 * Kruskal's algorithm : the MST is built as a forest. The edges are sorted by weights and considered in the increasing
 * order of the weights. Each vertex is in its tree in the forest. Each edge is taken and if the end nodes of the edge belong to 
 * disjoint trees , then they are merged and the edge is considered to be in the MST. Else, the edge is discarded.
 *
 */

//The reducer to form the minimum spanning tree based on Kruskal's algorithm
public class MSTReducer extends Reducer<IntWritable, Text, Text, Text> {

	Map<String, Set<String>> node_AssociatedSet = new HashMap<String, Set<String>>(); //Map to hold the information about the set of nodes that are in the same tree as the given node, each node is mapped to a set of nodes where the set represents the nodes that are present in the same tree

	public void reduce(IntWritable inputKey, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

		
		//values represent the source, destination pair that have inputKey as its edge weight

//		converting the type of inputKey to a Text
		String strKey = new String();
		strKey += inputKey;
		Text outputKey = new Text(strKey);
		
		for (Text val : values) {

			//boolean values to check if the two nodes belong to the same tree, useful for cycle detection
			boolean ignoreEdgeSameSet1 = false;
			boolean ignoreEdgeSameSet2 = false;
			boolean ignoreEdgeSameSet3 = false;
			
			Set<String> nodesSet = new HashSet<String>();				

			// split the srcDestination pair and add to the set, here the delimiter character to split the strings is ":" because 
			//the mapper used the same delimiter to append the source and destination, some other delimiter can also be used.
			String[] srcDest = val.toString().split(":");

			//getting the two nodes of an edge
			String src = srcDest[0]; 
			String dest = srcDest[1];

			//check if src and dest belong to the same tree/set, if so, ignore the edge
			ignoreEdgeSameSet1 = isSameSet(src, dest);
			
			//form the verticesSet
			nodesSet.add(src);
			nodesSet.add(dest);			
			
	
			ignoreEdgeSameSet2 = unionSet(nodesSet, src, dest);				
			ignoreEdgeSameSet3 = unionSet(nodesSet, dest, src);

			
			//if all the following three boolean values are false, then adding the edge to the tree will not form a cycle
			//therefore add the edge to the tree or write the edge to the output
			if (!ignoreEdgeSameSet1 && !ignoreEdgeSameSet2 && !ignoreEdgeSameSet3) {
				long weight = Long.parseLong(outputKey.toString());
				
				// increment the counter by the weight value, the counter holds the total weight of the minimum spanning tree
				context.getCounter(MSTCounters.totalWeight).increment(
						weight);

				// write the weight and srcDestination pair to the output
				context.write(outputKey, val);
			}
		}

	}

	//method to unite the set of the two nodes - node1 and node2, this is useful to add edges to the tree without forming cycles
	private boolean unionSet(Set<String> nodesSet, String node1, String node2) {
		boolean ignoreEdge = false;//boolean value to determine whether to ignore the edge
		
		//if the map does not contain the key, add the key, value pair
		if (!node_AssociatedSet.containsKey(node1)) {
			node_AssociatedSet.put(node1, nodesSet);
		} else {

			
			// get the set associated with the key
			Set<String> associatedSet = node_AssociatedSet.get(node1);
			Set<String > nodeSet = new HashSet<String>();
			nodeSet.addAll(associatedSet); 
			Iterator<String> nodeItr = nodeSet.iterator();
			Iterator<String> duplicateCheckItr = nodeSet.iterator();
			
			
			//first check if the second node is contained in any of the sets from node1 to nodeN
			// if so, ignore the edge as the two nodes belong to the same set/tree
			while(duplicateCheckItr.hasNext()){
				
				String node = duplicateCheckItr.next();
				if(node_AssociatedSet.get(node).contains(node2)){
				 ignoreEdge =  true;
				}
			}
			
			
			//if the associatedSet contains elements {node1 , node2, .., nodeN}
			//get the sets associated with each of the element from node1 to nodeN
			while (nodeItr.hasNext()) {

				String nextNode = nodeItr.next();
				
				if (!node_AssociatedSet.containsKey(nextNode)) {
					
					node_AssociatedSet.put(nextNode, nodesSet);
				}
				//add the src and dest to the set associated with each of the elements in the associatedSet
				//the src and dest will get added to the set associated with node1 to nodeN
				node_AssociatedSet.get(nextNode).addAll(nodesSet);

			}
		}
		return ignoreEdge;
		
	}
	
	//method to determine if the two nodes belong to the same set
	//this is done by iterating through the map and checking if any of the set contains the two nodes

	private boolean isSameSet(String src, String dest) {
		boolean ignoreEdge = false; //boolean value to check whether the edge should be ignored
		
		//iterating through the map
		for (Map.Entry<String, Set<String>> node_AssociatedSetValue : node_AssociatedSet
				.entrySet()) {
			
			
			Set<String> nodesInSameSet = node_AssociatedSetValue
					.getValue();
			
			//if the src and dest of an edge are in the same set, ignore the edge
			if (nodesInSameSet.contains(src)
					&& nodesInSameSet.contains(dest)) {
				ignoreEdge= true;
			}

		}

		return ignoreEdge;
	}	
	
}