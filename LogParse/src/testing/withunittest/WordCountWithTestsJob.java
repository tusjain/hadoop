package testing.withunittest;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WordCountWithTestsJob {
  private static final Logger LOG = LoggerFactory.getLogger(WordCountWithTestsJob.class);

  public final static String JOB_DATE_FORMAT_STRING = "yyyyMMdd-HHmmss";

  public void runJob(Path inFile, Path outFile, int reducerNum, Configuration conf) throws IOException, ClassNotFoundException, InterruptedException {
    //Initializing job
    //Configuration created with the default params (in here - local) and override by environment.
    //Will be overrided by dist env.

    //Set the job's name
    Job job = new Job(conf, "wordcount");

    //Init input format to be text input format
    job.setInputFormatClass(TextInputFormat.class);
    //Can be directory or file, local or hdfs ("hdfs://my-cluster:54310/user/ophchu")
    FileInputFormat.addInputPath(job, inFile);


    //Init mapper
    //Number of mapper set by the input format
    job.setMapperClass(WordCountWithTestsMapper.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);

    //Init partitioners
    job.setPartitionerClass(HashPartitioner.class);

    //Init reducer
    job.setReducerClass(WordCountWithTestsReducer.class);

    //User to set the number of reducers!
    job.setNumReduceTasks(reducerNum);

    //Init output format
    job.setOutputFormatClass(TextOutputFormat.class);

    //Create unique directory
    DateFormat df = new SimpleDateFormat(JOB_DATE_FORMAT_STRING);
    FileOutputFormat.setOutputPath(job, new Path(outFile, df.format(new Date())));

    //Run the job
    job.waitForCompletion(true);
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    WordCountWithTestsJob wcntJob = new WordCountWithTestsJob();
    wcntJob.runJob(new Path(args[0]), new Path(args[1]), 4, conf);
  }
}
