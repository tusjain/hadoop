package mapReduce;
import java.io.IOException;
import java.util.Iterator;

import reducerLogic.StubReducerLogic;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;


public class StubReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {

  @Override
  public void reduce(Text key, Iterator values, OutputCollector output, Reporter reporter) throws IOException {
	  
	  StubReducerLogic srl = new StubReducerLogic();
	  srl.stubReducerLogicMethod();

    /*
     * TODO implement
     */

  }
}