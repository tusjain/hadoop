package sorting.fearure.of.mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * 
 * @author Deepika Mohan
 * 
 *        
 *         Description : MSTMapper class that emits the input records in the form of <key, value> pairs where the key is the weight and the value is the source, destination pair. MapReduce has automatic sorting by keys after the map phase.
 *         This property can be used to sort the weights of the given graph.
 *         Therefore we have a mapper, the output of which
 *         will have the data sorted by keys which are weights in this program.
 *         So the reducer will get the edges in the order of increasing weight.
 *
 *         Input format : <key, value>: <automatically assigned key, weight<tab>source<tab>destination>
 *         
 *         Output format: <key, value>: < weight,source:destination>
 * 
 *         
 *         Hadoop version used : 0.20.2
 */

// the type parameters are the input keys type, the input values type, the
// output keys type, the output values type
public class MSTMapper extends Mapper<Object, Text, IntWritable, Text> {

		
	public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {

		 Text srcDestPair = new Text();

		 //StringTokenizer itr = new StringTokenizer(value.toString());
		 
		 String []inputTokens = value.toString().split("\t");  
			     
	     String weight = inputTokens[0] ;
	     // get the weight
	     int wt = Integer.parseInt(weight);
	          
	     IntWritable iwWeight = new IntWritable(wt);
	     // setting the source and destination to the key value
	     srcDestPair.set(inputTokens[1] + ":" + inputTokens[2]);
	 
	     //write <key, value> to context where the key is the weight, and the value is the sourceDestinationPair
	     context.write(iwWeight,srcDestPair);

		
	}
}