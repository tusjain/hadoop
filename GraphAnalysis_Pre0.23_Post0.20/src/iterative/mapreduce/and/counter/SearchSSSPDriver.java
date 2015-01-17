package iterative.mapreduce.and.counter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class SearchSSSPDriver extends Configured implements Tool{
	
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
		
		// the driver to execute the job and invoke the map/reduce functions

		public int run(String[] args) throws Exception {

			int iterationCount = 0; // counter to set the ordinal number of the intermediate outputs
			Job job;
			long terminationValue =0;
						
			// while there are more gray nodes to process
			while(iterationCount <= terminationValue){
				job = getJobConf(args); // get the job configuration
				String input, output;
				
				//setting the input file and output file for each iteration during the first time the user-specified file will be 
				//the input whereas for the subsequent iterations the output of the previous iteration will be the input
				if (iterationCount == 0){ // for the first iteration the input will be the first input argument
					input = args[0];
				}else{   // for the remaining iterations, the input will be the output of the previous iteration
					input = args[1] + iterationCount;
				}

				output = args[1] + (iterationCount + 1); // setting the output file

				FileInputFormat.setInputPaths(job, new Path(input)); // setting the input files for the job
				FileOutputFormat.setOutputPath(job, new Path(output)); // setting the output files for the job

				job.waitForCompletion(true); // wait for the job to complete

				Counters jobCntrs = job.getCounters();
				terminationValue = jobCntrs.findCounter(MoreIterations.numberOfIterations).getValue();//if the counter's value is incremented in the reducer(s), then there are more GRAY nodes to process implying that the iteration has to be continued.
				iterationCount++;
			}
			return 0;
		}

		public static void main(String[] args) throws Exception {
			int res = ToolRunner.run(new Configuration(), new SearchSSSPDriver(), args);
			if(args.length != 2){
				System.err.println("Usage: <in> <output name> ");
			}
			System.exit(res);
		}
		
		// method to set the configuration for the job and the mapper and the reducer classes
		private Job getJobConf(String[] args) throws Exception {
			JobInfo jobInfo = new JobInfo();
			return setupJob("ssspjob", jobInfo);
			}
}