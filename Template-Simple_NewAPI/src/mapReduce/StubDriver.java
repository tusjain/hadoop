package mapReduce;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class StubDriver extends Configured implements Tool{
	
	public int run(String[] args) throws Exception {
		Job job = JobBuilder.parseInputAndOutput(this, getConf(), args);
			if (job == null) {
			return -1;
		}
		
		//Exclude some files from input folders using regex
		//To use RegexFilter class supply "file.pattern" at CLI 
		//hadoop jar StubDriver -D file.pattern=.*regex.*
		FileInputFormat.setInputPathFilter(job, RegexFilter.class);
		
		//can be called repeatedly to build list of paths
	    // for example
	    //FileInputFormat.addInputPath(job, path1));
	    //FileInputFormat.addInputPath(job, path2));
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		
		//can be called repeatedly to build list of paths
	    // for example
	    //FileInputFormat.addInputPath(job, "path1, path2, path3"));
	    //FileInputFormat.addInputPath(job, "patha, pathb, pathc"));
	    //FileInputFormat.addInputPath(job, "comma separated paths"));
	    
	    
	    //if called repeatedly, only latest one will remain
	    //FileInputFormat.setInputPaths(job, "comma separated paths");
	    
	    //if called repeatedly, only latest one will remain
	    //FileInputFormat.setInputPaths(job, varags of Path);
		
		job.setInputFormatClass(TextInputFormat.class);
		
		job.setMapperClass(Mapper.class);
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(Text.class);
		
	    //No default combiner
	    //Assuming StubReducer logic is associative and commutative so setting Reducer as combiner
		job.setCombinerClass(StubReducer.class);
		
		job.setPartitionerClass(HashPartitioner.class);
		
		job.setNumReduceTasks(1);
		job.setReducerClass(Reducer.class);
		
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(Text.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		return job.waitForCompletion(true) ? 0 : 1;
		}
		public static void main(String[] args) throws Exception {
			int exitCode = ToolRunner.run(new StubDriver(), args);
			System.exit(exitCode);
		}
}