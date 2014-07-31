package mapReduce;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapRunner;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.lib.HashPartitioner;
import org.apache.hadoop.mapred.lib.IdentityMapper;
import org.apache.hadoop.mapred.lib.IdentityReducer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class StubDriver extends Configured implements Tool{
	
	public int run(String[] args) throws Exception {
		JobConf jobConf = JobBuilder.parseInputAndOutput(this, getConf(), args);
		if (jobConf == null) {
			return -1;
		}

		JobConf conf = new JobConf(getConf(), getClass());
	    conf.setJobName("Minimal MapReduce with explict sets");
	    
	    conf.setInputFormat(TextInputFormat.class);
	    
	    conf.setNumMapTasks(1);
	    conf.setMapperClass(IdentityMapper.class);
	    conf.setMapRunnerClass(MapRunner.class);
	    
	    conf.setMapOutputKeyClass(LongWritable.class);
	    conf.setMapOutputValueClass(Text.class);
	    
	    //No default combiner
	    //Since reducer logic is associative and commutative so setting REducer as combiner
	    conf.setCombinerClass(IdentityReducer.class);
	    
	    conf.setPartitionerClass(HashPartitioner.class);
	    
	    conf.setNumReduceTasks(1);
	    conf.setReducerClass(IdentityReducer.class);
	    
	    conf.setOutputKeyClass(LongWritable.class);
	    conf.setOutputValueClass(Text.class);
	    
	    conf.setOutputFormat(TextOutputFormat.class);
	    
	    //can be called repeatedly to build list of paths
	    // for example
	    //FileInputFormat.addInputPath(conf, path1));
	    //FileInputFormat.addInputPath(conf, path2));
	    FileInputFormat.addInputPath(conf, new Path(args[0]));
	    
	    //can be called repeatedly to build list of paths
	    // for example
	    //FileInputFormat.addInputPath(conf, "path1, path2, path3"));
	    //FileInputFormat.addInputPath(conf, "patha, pathb, pathc"));
	    //FileInputFormat.addInputPath(conf, "comma separated paths"));
	    
	    
	    //if called repeatedly, only latest one will remain
	    //FileInputFormat.setInputPaths(conf, "comma separated paths");
	    
	    //if called repeatedly, only latest one will remain
	    //FileInputFormat.setInputPaths(conf, varags of Path);
	    
	    //Exclude some files from input folders using regex
	    //To use RegexFilter class supply "file.pattern" at CLI 
	    //hadoop jar StubDriver -D file.pattern=.*regex.*
	    FileInputFormat.setInputPathFilter(conf, RegexFilter.class);
	    
	    
	    FileOutputFormat.setOutputPath(conf, new Path(args[1]));
	    
	    JobClient.runJob(conf);
		
		return 0;
	}
	
	public static void main(String args[]) throws Exception{
		 int exitCode = ToolRunner.run(new StubDriver(), args);
		  System.exit(exitCode);
	}
}