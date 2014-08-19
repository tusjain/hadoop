package OozieCoordinatorTimeInitiated;

//Source code for reducer
//--------------------------
//LogEventCountReducer.java
//--------------------------

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class LogEventCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

	@Override
	public void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		int intEventCount = 0;
		for (IntWritable value : values) {
			intEventCount += value.get();
		}
		context.write(key, new IntWritable(intEventCount));
	}
}