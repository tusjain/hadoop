package com.kashit.wordcount;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class WordCountMapperImproved extends Mapper<LongWritable, Text, Text, IntWritable> {

	private static final IntWritable ONE = new IntWritable(1);

	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		/*
		String s = value.toString();
		for (String word : s.split("\\W+")) {
			if (word.length() > 0) {
				context.write(new Text(word), ONE);
			}
		}
		*/
		processor( key,  value, context);
		// Map map = processor( key,  value);
	}
	
	
	private void processor(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
		String s = value.toString();
		for (String word : s.split("\\W+")) {
			if (word.length() > 0) {
				context.write(new Text(word), ONE);
			}
		}
		
	}
}
