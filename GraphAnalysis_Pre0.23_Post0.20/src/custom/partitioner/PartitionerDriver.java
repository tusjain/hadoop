package custom.partitioner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class PartitionerDriver extends Configured implements Tool{
	
	// the driver to execute two jobs and invoke the map/reduce functions
	public int run(String[] args) throws Exception {
		assignPartition(args[0], args[1]);
		return 0;
		}
		
	//executing the job to run the mapper, custom partitioner and the reducer
	private void assignPartition(String inputPath, String outputPath) throws Exception {

		Job partition_job = getJobConfPartition();
		String input = inputPath;
		String output = outputPath;

		FileSystem fs = FileSystem.getLocal(partition_job.getConfiguration());
		Path opPath = new Path(output);
		fs.delete(opPath, true);

		FileInputFormat.setInputPaths(partition_job, new Path(input)); // setting the input files for the job
		FileOutputFormat.setOutputPath(partition_job, new Path(output)); // setting the output files for the job
		partition_job.waitForCompletion(true);
		}

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new PartitionerDriver(), args);
		if (args.length != 2) {
			System.err.println("Usage: SamplePartitioner <in> <output>");
			System.exit(2);
			}
		System.exit(res);
		}
		
	// method to get job configuration for the customized partitioning MapReduce program
	private Job getJobConfPartition() throws Exception {
		JobInfo jobInfo = new JobInfo();
		Job job = setupJob("partition", jobInfo);
		job.setPartitionerClass(AgePartitioner.class);
		return job;
		}
		
	// method to set the configuration for the job and the mapper and the reducer classes
	protected Job setupJob(String jobName,JobInfo jobInfo) throws Exception {
		Job job = new Job(new Configuration(), jobName);
		// set the several classes
		job.setJarByClass(jobInfo.getJarByClass());

		//set the mapper class
		job.setMapperClass(jobInfo.getMapperClass());

		//the combiner class is optional, so set it only if it is required by the program
		if (jobInfo.getCombinerClass() != null){
			job.setCombinerClass(jobInfo.getCombinerClass());
		}
		//set the reducer class
		job.setReducerClass(jobInfo.getReducerClass());
			
		//the number of reducers is set to 3, this can be altered according to the program's requirements
		job.setNumReduceTasks(3);
		
		// set the type of the output key and value for the Map & Reduce functions
		job.setOutputKeyClass(jobInfo.getOutputKeyClass());
		job.setOutputValueClass(jobInfo.getOutputValueClass());
			
		return job;
		}
}