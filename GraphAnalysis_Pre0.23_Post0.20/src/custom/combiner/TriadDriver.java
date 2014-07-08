package custom.combiner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import HadoopGTK.EnumerateTriads;
import HadoopGTK.EnumerateTriads.AugmentDegreeForEdge_Reducer;
import HadoopGTK.EnumerateTriads.AugmentDegreeIdentityMapper;
import HadoopGTK.EnumerateTriads.AugmentDegreeMapper;
import HadoopGTK.EnumerateTriads.AugmentDegreeReducer;

public class TriadDriver extends Configured implements Tool {
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
	
	// method to get the job configuration for the first job for enumerating the triads
	private Job getJobConfTriadJob() throws Exception {
		JobInfo jobInfo = new JobInfo();
		return setupJob("EnumerateTriadFirst", jobInfo);
		}

	// method to run the second job to augment edges with degree
	private void augmentEdgesWithDegreesSecondJob(String inputPath, String outputPath) throws Exception {
			Job augmentDegree_job = getJobConfAugmentDegreeSecond();
			String input = inputPath;
			String output = outputPath;
			FileSystem fs = FileSystem.getLocal(augmentDegree_job.getConfiguration());
			Path opPath = new Path(output);
			fs.delete(opPath, true);
			FileInputFormat.setInputPaths(augmentDegree_job, new Path(input)); // setting the input files for the job
			FileOutputFormat.setOutputPath(augmentDegree_job, new Path(output)); // setting the output files for the job
			augmentDegree_job.waitForCompletion(true);
		}

	// method to configure the second job to augment edges with degree
	private Job getJobConfAugmentDegreeSecond() throws Exception {
		JobInfo jobInfo = new JobInfo() {
			@Override
			public Class<? extends Reducer> getReducerClass() {
				return AugmentDegreeForEdge_Reducer.class;
			}
			@Override
			public Class<?> getOutputValueClass() {
				return Text.class;
			}
			@Override
			public Class<?> getOutputKeyClass() {
				return Text.class;
			}
			@Override
			public Class<? extends Mapper> getMapperClass() {
				return AugmentDegreeIdentityMapper.class;
			}
			@Override
			public Class<?> getJarByClass() {
				return EnumerateTriads.class;
			}
			@Override
			public Class<? extends Reducer> getCombinerClass() {
				return null;
			}
		};
		return setupJob("AugmentDegreeSecond", jobInfo);
	}

	// method to get the configuration of the job to augment the edges with the degree
	private Job getJobConfAugmentDegreeFirst() throws Exception {
		JobInfo jobInfo = new JobInfo() {
			@Override
			public Class<? extends Reducer> getReducerClass() {
				return AugmentDegreeReducer.class;
			}
			@Override
			public Class<?> getOutputValueClass() {
				return Text.class;
			}
			@Override
			public Class<?> getOutputKeyClass() {
				return Text.class;
			}
			@Override
			public Class<? extends Mapper> getMapperClass() {
				return AugmentDegreeMapper.class;
			}
			@Override
			public Class<?> getJarByClass() {
				return EnumerateTriads.class;
			}
			@Override
			public Class<? extends Reducer> getCombinerClass() {
				return null;
			}
		};

	return setupJob("AugmentDegree", jobInfo);
	}

	// method to run the first job to augment edges with degree
	private void augmentEdgesWithDegreesFirstJob(String inputPath, String outputPath) throws Exception {
		Job augmentDegree_job = getJobConfAugmentDegreeFirst();
		String input = inputPath;
		String output = outputPath;
		FileSystem fs = FileSystem.getLocal(augmentDegree_job.getConfiguration());
		Path opPath = new Path(output);
		fs.delete(opPath, true);
		FileInputFormat.setInputPaths(augmentDegree_job, new Path(input)); // setting the input files for the job
		FileOutputFormat.setOutputPath(augmentDegree_job, new Path(output)); // setting the output files for the job
		augmentDegree_job.waitForCompletion(true);
		}

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new EnumerateTriads(), args);
		if (args.length != 4) {
			System.err.println("Usage: Enumerate triads <inAugmentDegree> <intermediateAugmentdegree> <outputAugmentDegree> <outputTriad>");
			System.exit(2);
		}
		System.exit(res);
	}
	
	public int run(String[] args) throws Exception {
		augmentEdgesWithDegreesFirstJob(args[0], args[1]);
		augmentEdgesWithDegreesSecondJob(args[1], args[2]);
		enumerateTriadsJob(args[2], args[3]);
		return 0;
	}
	// method to execute or run the first job for enumerating the triads
	private void enumerateTriadsJob(String inputPath, String outputPath) throws Exception {
		Job Triad_job = getJobConfTriadJob();
		String input = inputPath;
		String output = outputPath;
		FileSystem fs = FileSystem.getLocal(Triad_job.getConfiguration());
		Path opPath = new Path(output);
		fs.delete(opPath, true);
		FileInputFormat.setInputPaths(Triad_job, new Path(input)); // setting the input files for the job
		FileOutputFormat.setOutputPath(Triad_job, new Path(output)); // setting the output files for the job
		Triad_job.waitForCompletion(true);
		}
}