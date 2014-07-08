package user.defined.configuration.parameters;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class ReachabilityDriver extends Configured implements Tool{
	// method to set the configuration for the job and the mapper and the reducer classes
	private Job getJobConf(String[] args) throws Exception {
		JobInfo jobInfo = new JobInfo();
		return setupJob("reachabilityjob", jobInfo);
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
	
	// the driver to execute the job and invoke the map/reduce functions
	public int run(String[] args) throws Exception {

			int iterationCount = 0; // counter to set the ordinal number of the intermediate outputs

			Job job;
		    long terminationValue =0;
			long isDestinationReached = 0;
		
			String src = args[2];
			String dest = args[3];
			
		
			//loop till there are more gray nodes to process and the destination is not reached
			while(iterationCount <= terminationValue && isDestinationReached == 0L){

				
				job = getJobConf(args); // get the job configuration
				String input;
				if (iterationCount == 0) // for the first iteration , the input will be the input argument
					input = args[0];
				else
					// for the remaining iterations, the input will be the output of the previous iteration
					input = args[1] + iterationCount;

				String output = args[1] + (iterationCount + 1); // setting the output file

				job.getConfiguration().set("source", src); //setting the source to be used in mapper or reducer
				job.getConfiguration().set("destination", dest); //setting the destination to be used in mapper or reducer
				
				FileInputFormat.setInputPaths(job, new Path(input)); // setting the input files for the job
				FileOutputFormat.setOutputPath(job, new Path(output)); // setting the output files for the job
				
				job.waitForCompletion(true); // wait for the job to complete

				Counters jobCntrs = job.getCounters();
				terminationValue = jobCntrs.findCounter(ReachabilityCounters.numberOfIterations).getValue(); //get the terminationValue that will determine whether to continue the iteration
				
				isDestinationReached = jobCntrs.findCounter(ReachabilityCounters.isDestinationFound).getValue();//get the isDestinationReached value that will notify if the destination is reached
			
				
				iterationCount++;
			
			}

			//Check whether there is a path between the source and the destination based on the isDestinationReached counter value and print the result

			if (isDestinationReached == 0L){
				System.out.println("There is no path between "+src+" and "+dest);
			}
			else{
				System.out.println("There is a path between "+src+" and "+dest);
			}
			return 0;
		}

		public static void main(String[] args) throws Exception {

			int res = ToolRunner.run(new Configuration(), new ReachabilityDriver(), args);
			if(args.length != 4){
				System.err.println("Usage: <in> <out> <sourceNode> <destinationNode>");
			}
			System.exit(res);
		}
}