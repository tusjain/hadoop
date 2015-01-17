package custom.partitioner;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

//The data belonging to the same partition go to the same reducer. In a particular partition, all the values with the same key are iterated and the person with the maximum score is found.
//Therefore the output of the reducer will contain the male and female maximum scorers in each of the 3 age categories.

// the type parameters are the input keys type, the input values type, the
// output keys type, the output values type

public class ParitionReducer extends Reducer<Text, Text, Text, Text> {

	@Override
	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

		int maxScore = Integer.MIN_VALUE;
		
		String name = " ";
		String age = " ";
		String gender = " ";
		int score = 0;
		//iterating through the values corresponding to a particular key
		for(Text val: values){
		
			String [] valTokens = val.toString().split("\\t");
			score = Integer.parseInt(valTokens[2]);
		
            //if the new score is greater than the current maximum score, update the fields as they will be the output of the reducer after all the values are processed for a particular key			
			    if(score > maxScore){
				name = valTokens[0];
				age = valTokens[1];
				gender = key.toString();
                maxScore = score;
			}
		}
		context.write(new Text(name), new Text("age- "+age+"\t"+gender+"\tscore-"+maxScore));
	}
}