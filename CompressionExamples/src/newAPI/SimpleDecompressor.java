//Take a compressed file as in input file for above code an run it, it will decompress the file
package newAPI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class SimpleDecompressor extends Configured implements Tool{
	public int run(String[] args) throws Exception{
		Configuration conf = new Configuration();
		Job job = new Job(conf, "simpleDecompressor");
		
		job.setJarByClass(SimpleDecompressor.class);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
	
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		return (job.waitForCompletion(true) ? 0:1);
	}
	
	public static void main(String[] args) throws Exception{
		int eCode = ToolRunner.run(new SimpleDecompressor(), args);
		System.exit(eCode);
	}
}
