package OozieWorkflowJavaMainAction;

//Source code for Diver
//--------------------------
//LogEventCount.java
//--------------------------

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
public class LogEventCount {
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.printf(
					"Usage: Airawat.Oozie.Samples.LogEventCount <input dir> <output dir>\n");
			System.exit(-1);
		}
		//Instantiate a Job object for your job's configuration.
		Job job = new Job();
		//Job jar file
		job.setJarByClass(LogEventCount.class);
		//Job name
		job.setJobName("Syslog Event Rollup");
		//Paths
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		//Mapper and reducer classes
		job.setMapperClass(LogEventCountMapper.class);
		job.setReducerClass(LogEventCountReducer.class);
		//Job's output key and value classes
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		//Number of reduce tasks
		job.setNumReduceTasks(3);
		//Start the MapReduce job, wait for it to finish.
		boolean success = job.waitForCompletion(true);
		System.exit(success ? 0 : 1);
	}
}