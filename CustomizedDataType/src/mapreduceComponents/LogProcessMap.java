package mapreduceComponents;

import inputs.LogWritable;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

//public class LogProcessMap extends MapReduceBase implements Mapper<LongWritable, Text, Text, LogWritable> {
	
public class LogProcessMap extends MapReduceBase implements Mapper<LongWritable, LogWritable, Text, LogWritable> {
	
	
	
@Override
/*
public void map(LongWritable key, Text value, OutputCollector<Text, LogWritable> output, Reporter reporter) throws IOException {
	}

*/

public void map(LongWritable key, LogWritable value, OutputCollector<Text, LogWritable> output, Reporter reporter) throws IOException {
	
	
}



}