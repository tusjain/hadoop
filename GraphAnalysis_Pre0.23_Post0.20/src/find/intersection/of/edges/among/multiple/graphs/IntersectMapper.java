package find.intersection.of.edges.among.multiple.graphs;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Mapper class that has the map method that outputs intermediate keys and values to be processed by the IntersectReducer.
 * 
 * The type parameters are the type of the input key, the type of the input values, the type of the output key and the type of the output values
 * 
 * Input format : graphId<tab>source<tab>destination
 * Output format : <source:destination, graphId>
**/
public class IntersectMapper extends Mapper<LongWritable, Text, Text, Text> {

	private Text gId = new Text(); // graphId
	private Text srcDestPair = new Text(); // source, destination pair
	
	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		StringTokenizer itr = new StringTokenizer(value.toString());
		while (itr.hasMoreTokens()) {

			// get the graph id
			gId.set(itr.nextToken());
			// setting the key as source and the value as the destination 
			//The source and the destination are delimited by ":" here
			srcDestPair.set(itr.nextToken() + ":" + itr.nextToken());

			//emit key, value pair from mapper
			//here the key is the source:destination pair and the graph id is the value
			context.write(srcDestPair, gId);
		}
	}
}