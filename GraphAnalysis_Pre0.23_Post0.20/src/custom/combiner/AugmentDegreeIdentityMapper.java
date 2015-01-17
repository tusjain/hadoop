package custom.combiner;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

// The second mapper in the process of augmenting the edges with the
// degrees. This is an identity mapper where the output key is the edge and
// the output value is the degree of one of the nodes in the edge
public class AugmentDegreeIdentityMapper extends Mapper<Object, Text, Text, Text> {
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		String[] tokens = value.toString().split("\\s");
		context.write(new Text(tokens[0]), new Text(tokens[1]));
	}
}