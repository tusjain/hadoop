package custom.partitioner;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

//PartitionMapper parses the input records and emits the key, value pairs suitable for the partitioner and the reducer.

	//mapper output format : gender is the key, the value is formed by concatenating the name, age and the score

	// the type parameters are the input keys type, the input values type, the
	// output keys type, the output values type
	public class PartitionMapper extends Mapper<Object, Text, Text, Text> {

		// The Context type is an inner class of Mappable and it seems to be
		// where
		// you pass back output key-values for passing on to the reduce stage

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String[] tokens = value.toString().split("\t");

			String gender = tokens[2].toString();
			String nameAgeScore = tokens[0]+"\t"+tokens[1]+"\t"+tokens[3];
			
			context.write(new Text(gender), new Text(nameAgeScore));
		}
	}