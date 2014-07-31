//here is no mapper and reducer.Notice the two lines which are doing the job of compression.Instead of 
//defalte type any other compression format can be taken.

package newAPI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.compress.DefaultCodec;
import org.apache.hadoop.io.compress.BZip2Codec;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class SimpleCompressor extends Configured implements Tool{
	
	public int run(String[] args) throws Exception{
		Configuration conf = new Configuration();
		Job job = new Job(conf, "simpleCompressor");
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		FileOutputFormat.setCompressOutput(job, true);
		//FileOutputFormat.setOutputCompressorClass(job, DefaultCodec.class);
		
		FileOutputFormat.setOutputCompressorClass(job, BZip2Codec.class);
		job.setJarByClass(SimpleDecompressor.class);
		
		
		return (job.waitForCompletion(true) ? 0:1);
	}
	
	public static void main(String[] args) throws Exception{
		int eCode = ToolRunner.run(new SimpleDecompressor(), args);
		System.exit(eCode);
	}
}