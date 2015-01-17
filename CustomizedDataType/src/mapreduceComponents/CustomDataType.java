package mapreduceComponents;

import inputs.LogWritable;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

public class CustomDataType {
     public static void main(String[] args) throws Exception {
             JobConf newconf = new JobConf(CustomDataType.class);
             newconf.setJobName("Custom Data Type");
             newconf.setOutputKeyClass(Text.class);
             newconf.setOutputValueClass(IntWritable.class);
             newconf.setMapperClass(LogProcessMap.class);
             newconf.setReducerClass(LogProcessReduce.class);
            
             newconf.setMapOutputKeyClass(Text.class);
             newconf.setMapOutputValueClass(LogWritable.class);
             newconf.setInputFormat(TextInputFormat.class);
             newconf.setOutputFormat(TextOutputFormat.class);
             FileInputFormat.setInputPaths(newconf, new Path(args[0]));
             FileOutputFormat.setOutputPath(newconf, new Path(args[1]));
             JobClient.runJob(newconf);
      }
}