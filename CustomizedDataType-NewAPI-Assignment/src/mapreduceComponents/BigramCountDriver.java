package mapreduceComponents;



import java.io.IOException;

 import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.IntWritable;

import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class BigramCountDriver {

    public static void main(String args[]) throws IOException, InterruptedException, ClassNotFoundException {

        if (args.length != 2) {

            System.err.println("Inavlid Command!");

            System.err.println("Usage: BigramCount <input type=" + text + " /> <output>");

            System.exit(0);

        }

         Configuration conf = new Configuration();

        conf.set("mapreduce.jobtracker.address", "local");

        conf.set("fs.defaultFS","file:///");

         Job job = new Job(conf);

         job.setJarByClass(BigramCountDriver.class);

        job.setJobName("Word Count");

         FileInputFormat.addInputPath(job, new Path(args[0]));

        FileOutputFormat.setOutputPath(job, new Path(args[1]));

         job.setMapperClass(BigramCountMapper.class);

        job.setReducerClass(BigramCountReducer.class);

         job.setMapOutputKeyClass(inputs.TextPair.class);

        job.setMapOutputValueClass(IntWritable.class);

         job.setOutputKeyClass(Text.class);

        job.setOutputValueClass(IntWritable.class);

         System.exit(job.waitForCompletion(true) ? 0 : 1);

    }
}