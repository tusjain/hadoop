package wordCount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.StringTokenizer;

public class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
  private static final Logger LOG = LoggerFactory.getLogger(WordCountMapper.class);
  private final static IntWritable one = new IntWritable(1);
  private Text word = new Text(); //Reuse of the Text object - prevent creation of millions objects

  public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
    //Text input format:
    //Mappers by blocks
    //Key == position in file
    //Value == line text
    String line = value.toString();
    StringTokenizer tokenizer = new StringTokenizer(line);
    while (tokenizer.hasMoreTokens()) {
      word.set(tokenizer.nextToken());
      //Write the word + '1'
      context.write(word, one);
    }
  }
}
