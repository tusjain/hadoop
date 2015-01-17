import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
public class WithToolWordCountDriver extends Configured implements Tool{

	public int run(String[] args) throws Exception {
		//if (args.length != 2) {
		if (args.length < 2) {
			/*System.out.println("args[0]   " + args[0]);
			System.out.println("args[1]   " + args[1]);
			System.out.println("args[2]   " + args[2]);
			System.out.println("args[3]   " + args[3]);
			System.out.println("args[4]   " + args[4]); */
			System.out.println("usage: [input] [output]");
			System.exit(-1);
		}

		// When implementing tool
		Configuration conf = this.getConf();
		
		// Create job
		Job job = new Job(conf, "Tool Job");
		job.setJarByClass(WithToolWordCountDriver.class);

		// Setup MapReduce job
		// Do not specify the number of Reducer
		job.setMapperClass(WordCountMapper.class);
		job.setReducerClass(WordCountReducer.class);

		// Specify key / value
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		// Input
		//If executing from Command prompt
		FileInputFormat.addInputPath(job, new Path(args[1]));
		//If executing from Eclipse
		FileInputFormat.addInputPath(job, new Path(args[0]));
		job.setInputFormatClass(TextInputFormat.class);

		// Output
		//If executing from Command prompt
		FileOutputFormat.setOutputPath(job, new Path(args[2]));
		//If executing from Eclipse
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setOutputFormatClass(TextOutputFormat.class);
		
		// Execute job and return status
		return job.waitForCompletion(true) ? 0 : 1;

	}
	
	public static void main(String args[]) throws Exception{
		// Let ToolRunner handle generic command line options
		int res = ToolRunner.run(new Configuration(), new WithToolWordCountDriver(), args);
		}
	/*
 	public static void main(String[] args) throws Exception{
	   if (args.length != 2) {
          System.out.println("usage: [input] [output]");
          System.exit(-1);
        }

        Job job = Job.getInstance(new Configuration());
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class); 

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setJarByClass(WithToolWordCountDriver.class);

        job.submit();
 	}
	 */
}