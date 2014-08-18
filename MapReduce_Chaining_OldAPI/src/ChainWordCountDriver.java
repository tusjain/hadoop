import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.lib.ChainMapper;
import org.apache.hadoop.mapred.lib.ChainReducer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class ChainWordCountDriver extends Configured implements Tool { 
public int run(String[] args) throws Exception {
        JobConf conf = new JobConf(getConf(), ChainWordCountDriver.class);
        conf.setJobName("wordcount");
     
        Path outputPath = new Path("/home/impadmin/testdata/CustomerOutput");
        FileSystem  fs = FileSystem.get(new URI(outputPath.toString()), conf);
        //It will delete the output directory if it already exists. don't need to delete it  manually  
        fs.delete(outputPath);
      
        //Setting the input and output path
        FileInputFormat.setInputPaths(conf, "/home/impadmin/testdata/Customer");
        FileOutputFormat.setOutputPath(conf, outputPath);

        //Considering the input and output as text file set the input & output format to TextInputFormat
        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        JobConf mapAConf = new JobConf(false);
        ChainMapper.addMapper(conf, TokenizerMapper.class, LongWritable.class, Text.class, Text.class, IntWritable.class, true, mapAConf);     
        

            //addMapper will take global conf object and mapper class ,input and output type for this mapper and output key/value have to be sent by value or by reference and localJObconf specific to this call
       

        JobConf mapBConf = new JobConf(false);
        ChainMapper.addMapper(conf, UpperCaserMapper.class, Text.class, IntWritable.class, Text.class, IntWritable.class, true, mapBConf);

        JobConf reduceConf = new JobConf(false);
        ChainReducer.setReducer(conf, WordCountReducer.class, Text.class, IntWritable.class, Text.class, IntWritable.class, true, reduceConf);

       JobConf mapCConf = new JobConf(false);
       ChainReducer.addMapper(conf, LastMapper.class, Text.class, IntWritable.class, Text.class, IntWritable.class, true, mapCConf);
        JobClient.runJob(conf);
        return 0;
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new ChainWordCountDriver(), args);
        System.exit(res);
    }
} 