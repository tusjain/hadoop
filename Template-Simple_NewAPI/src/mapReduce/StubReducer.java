package mapReduce;
import java.io.IOException;

import reducerLogic.StubReducerLogic;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class StubReducer extends Reducer<Text, IntWritable, Text, DoubleWritable> {

  @Override
  public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
	  
	  StubReducerLogic srl = new StubReducerLogic();
	  srl.stubReducerLogicMethod();

    /*
     * TODO implement
     */

  }
}