import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.io.compress.SnappyCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class MaxTemperatureDriverWithCompression {
	 public static void main(String[] args) throws Exception {
		    if (args.length != 2) {
		      System.err.println("Usage: MaxTemperature <input path> <output path>");
		      System.exit(-1);
		    }
		    
		    Job job = new Job();
		    job.setJarByClass(MaxTemperatureDriverWithCompression.class);
		    job.setJobName("Max temperature with Compression");

		    FileInputFormat.addInputPath(job, new Path(args[0]));
		    FileOutputFormat.setOutputPath(job, new Path(args[1]));
		    
		    // set compressed type
		    job.getConfiguration().setBoolean("mapred.compress.map.output", true);
		    job.getConfiguration().setClass("mapred.map.output.compression.codec", SnappyCodec.class, CompressionCodec.class);
		    
		    //job.getConfiguration().setClass("mapred.map.output.compression.codec", GzipCodec.class, CompressionCodec.class);
		       
		    
		    job.setMapperClass(MaxTemperatureMapper.class);
		    job.setReducerClass(MaxTemperatureReducer.class);

		    job.setOutputKeyClass(Text.class);
		    job.setOutputValueClass(IntWritable.class);
		    
		    System.exit(job.waitForCompletion(true) ? 0 : 1);
		  }
}
