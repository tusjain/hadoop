package custom.combiner;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

	//The mapper class for enumerating the open triads (pairs of edges of the form {(A,B),(B,C)}
	// The mapper records each edge under its low degree member
	// Therefore the output key of the mapper will be the node with the lowest degree and the value would be the edge
	public class TriadMapper extends Mapper<Object, Text, Text, Text> {
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

			String[] tokens = value.toString().split("\\s");
			String[] splitTokens = tokens[1].split("\\|");

			String[] node1 = splitTokens[1].split("[()]");

			String[] degreeToken1 = splitTokens[1].split("\\)");
			int degree1 = Integer.parseInt(degreeToken1[1]);

			String[] node2 = splitTokens[2].split("[()]");
			String[] degreeToken2 = splitTokens[2].split("\\)");
			int degree2 = Integer.parseInt(degreeToken2[1]);

			Text edge = new Text(tokens[0]);

			if (degree1 <= degree2) {
				context.write(new Text(node1[1]), edge);
			} else {
				context.write(new Text(node2[1]), edge);
			}

		}

	}