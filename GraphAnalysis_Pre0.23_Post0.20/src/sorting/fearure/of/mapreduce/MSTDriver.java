package sorting.fearure.of.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import HadoopGTK.MST;

import user.defined.configuration.parameters.JobInfo;

public class MSTDriver extends Configured implements Tool {
	
	//the method to call the functions that run the jobs
		public int run(String[] args) throws Exception {

			formMSTJob(args[0], args[1]);

			return 0;

		}

		//method to run the job that forms the MST
		private void formMSTJob(String inputPath, String outputPath)
				throws Exception {
			Job mstJob = getMSTJobConf(); //get the job configurations

			FileInputFormat.setInputPaths(mstJob, new Path(inputPath)); // setting the input files for the job
			FileOutputFormat.setOutputPath(mstJob, new Path(outputPath)); // setting the output files for the job

			mstJob.waitForCompletion(true);

			Counters jobCntrs = mstJob.getCounters();//get all the counters associated with mstJob

			long totalWeight = jobCntrs.findCounter(MSTCounters.totalWeight)
					.getValue();
			
			System.out.println("The total weight of the MST is " + totalWeight	);

		}

	//get the job configuration  for formMST mapper and reducer
		private Job getMSTJobConf() throws Exception {
			
			JobInfo jobInfo = new JobInfo();
			return setupJob("formMST", jobInfo);
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
				
				//main program
				public static void main(String[] args) throws Exception {

					int res = ToolRunner.run(new Configuration(), new MST(), args);
					if (args.length != 2) {
						System.err
								.println("Usage: MST <in> <output > ");
						System.exit(2);
					}
					System.exit(res);
				}

}
