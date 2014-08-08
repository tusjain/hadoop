package mapReduce;

import java.io.IOException;

import mapperLogic.StubMapperLogic;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class StubMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

  @Override
  public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

    StubMapperLogic sml = new StubMapperLogic();
    sml.stubMapperLogicMethod();
    
	 /*
     * TODO implement
     */

  }
}