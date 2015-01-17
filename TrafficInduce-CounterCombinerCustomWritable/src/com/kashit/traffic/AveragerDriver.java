package com.kashit.traffic;


import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class AveragerDriver {
  public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
    Configuration conf = new Configuration();
    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
    Job job = new Job(conf);
    job.setJarByClass(AveragerDriver.class);
    job.setMapperClass(AveragerMapper.class);
    job.setReducerClass(AveragerReducer.class);
    job.setCombinerClass(AveragerReducer.class);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(AverageWritable.class);
    job.setInputFormatClass(TextInputFormat.class);
    
    FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
    FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

    job.waitForCompletion(true);
  }
}