package custom.combiner;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

	//AugmentDegreeMapper is a part of the job to augment the edges with the degree of the nodes present in it
	// the type parameters are the input keys type, the input values type, the output keys type, the output values type
	public class AugmentDegreeMapper extends Mapper<Object, Text, Text, Text> {
		
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			// the input format for the mapper is Node1<tab>Node2
			// the output format for the mapper is in the form of the <key,value> pairs where the key is one of the nodes and the value is the edge
			// for example, for the input Node1<tab>Node2, the mapper will emit : <Node1, <Node1, Node2>> and <Node2, <Node1,Node2>>

			String[] tokens = value.toString().split(",");

			context.write(new Text(tokens[0]), value);
			context.write(new Text(tokens[1]), value);
		}
	}