package multiple.mappers.and.reducers;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import user.defined.configuration.parameters.JobInfo;


public class EulerDriver extends Configured implements Tool{
	// method to set the configuration for the job and the mapper and the reducer classes
		private Job getJobConf() throws Exception {
			JobInfo jobInfo = new JobInfo();
			return setupJob("graphsearch", jobInfo);
		}
		
		// the driver to execute two jobs and invoke the map/reduce functions

		public int run(String[] args) throws Exception {

			long isNotConnected = 0, isNotEvenDegree = 0;

			isNotConnected = connectivityJob(args[0], args[1]);
			isNotEvenDegree = degreeJob(args[0], args[2]);

			if (isNotConnected == 0 && isNotEvenDegree > 0)
				System.out
						.println("connected but not all vertices are even degree ");
			else if (isNotConnected > 0 && isNotEvenDegree == 0)
				System.out
						.println("not connected but all vertices are of even degree");
			else if (isNotConnected > 0 && isNotEvenDegree > 0)
				System.out
						.println("not connected and not all vertices are of even degree");
			else if (isNotConnected == 0 && isNotEvenDegree == 0)
				System.out
						.println("Connected and all vertices are of even degree. Euler tour is present");

			return 0;

		}
		
		// executing the job to determine if the graph is connected
		private long connectivityJob(String inputPath, String outputPath)
				throws Exception {

			int iterationCount = 0; // counter to set the ordinal number of the
			// intermediate outputs

			Job job = null;
			long terminationValue = 0;

			// while there are more gray nodes to process
			while (iterationCount <= terminationValue) {

				job = getJobConf(); // get the job configuration
				String input;
				if (iterationCount == 0) // for the first iteration , the input will
					// be the input_graph
					input = inputPath;
				else
					// for the remaining iterations, the input will be the output of
					// the previous iteration
					input = outputPath + iterationCount;

				String output = outputPath + (iterationCount + 1); // setting the output file

				FileInputFormat.setInputPaths(job, new Path(input)); // setting the input files for the job
				FileOutputFormat.setOutputPath(job, new Path(output)); // setting the output files for the job

				iterationCount++;

				job.waitForCompletion(true); // wait for the job to complete

				Counters jobCntrs = job.getCounters();
				terminationValue = jobCntrs.findCounter(
						EulerCounters.numberOfIterations).getValue();

			}

			Counters jobCntrs = job.getCounters();
			long connected = jobCntrs.findCounter(EulerCounters.isNotConnected)
					.getValue();
			return connected;
		}
		
		// executing the job to determine if every vertex is of even degree
		private long degreeJob(String inputPath, String outputPath)
				throws Exception {

			Job degree_job = getJobConfDegree();

			FileInputFormat.setInputPaths(degree_job, new Path(inputPath)); // setting the input files for the job
			FileOutputFormat.setOutputPath(degree_job, new Path(outputPath)); // setting the output files for the job

			degree_job.waitForCompletion(true);

			Counters jobCntrs = degree_job.getCounters();
			long isEvenDegree = jobCntrs.findCounter(EulerCounters.isNotEvenDegree)
					.getValue();
			return isEvenDegree;

		}

		// get the Job configurations for the degree job
		private Job getJobConfDegree() throws Exception {
			JobInfo jobInfo = new JobInfo();
			
			return setupJob("degree", jobInfo);
			
			
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

		public static void main(String[] args) throws Exception {

			int res = ToolRunner.run(new Configuration(), new EulerDriver(), args);
			if (args.length != 3) {
				System.err
						.println("Usage: Euler tour <in> <output_search> <output_degree>");
				System.exit(2);
			}
			System.exit(res);
		}

}
