package find.intersection.of.edges.among.multiple.graphs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class IntersectDriver extends Configured implements Tool{
	
	// method to set the configuratiomn for the job and the mapper and the reducer classes
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
		
		@Override
		public int run(String[] args) throws Exception {
			Job job = getJobConf(args);

			FileInputFormat.addInputPath(job, new Path(args[0]));// specify the input path to be the first command-line argument passed by the user
			FileOutputFormat.setOutputPath(job, new Path(args[1]));// specify the output path to be the second command-line argument passed by the user

			return job.waitForCompletion(true) ? 0 : 1;// wait for the job to complete
		}
		// method to set the configuration for the job and the mapper and the reducer classes
		private Job getJobConf(String[] args) throws Exception {

			JobInfo jobInfo = new JobInfo();
			
			return setupJob("intersect", jobInfo);
		}
		
		public static void main(String[] args) throws Exception {

			int res = ToolRunner.run(new Configuration(), new IntersectDriver(), args);
			if (args.length != 2) {
				System.err.println("Usage: Intersect <in> <out>" );
				System.exit(2);
			}
			System.exit(res);
		}
		

}
