import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class SalaryCalculatorDriver extends Configured implements Tool{
      public int run(String[] args) throws Exception {
            //getting configuration object and setting job name
            Configuration conf = getConf();
        Job job = new Job(conf, "Salary Calculator Demo");
       
        //setting the class names
        job.setJarByClass(SalaryCalculatorDriver.class);
        job.setMapperClass(SalaryCalculatorMapper.class);
        job.setReducerClass(SalaryCalculatorReducer.class);

        //setting the output data type classes
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        //to accept the hdfs input and outpur dir at run time
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new SalaryCalculatorDriver(), args);
        System.exit(res);
    }
}