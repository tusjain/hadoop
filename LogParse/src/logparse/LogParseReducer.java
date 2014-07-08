package logparse;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testing.nounitest.WordCountNoTestsReducer;

import java.io.IOException;

public class LogParseReducer extends Reducer<Text, Text, Text, IntWritable> {
  private static final Logger LOG = LoggerFactory.getLogger(WordCountNoTestsReducer.class);

  private enum Status {
    LEVELS_NUM, TOTAL_LINES
  }

  public void reduce(Text key, Iterable<Text> values, Context context)
          throws IOException, InterruptedException {
    context.setStatus(String.format("Going to process: %s", key.toString()));
    //Count levels
    context.getCounter(Status.LEVELS_NUM).increment(1);
    int sum = 0;
    for (Text val : values) {
      //Count total lines
      context.getCounter(Status.TOTAL_LINES).increment(1);
      sum += 1;
    }
    context.write(key, new IntWritable(sum));
  }
}
