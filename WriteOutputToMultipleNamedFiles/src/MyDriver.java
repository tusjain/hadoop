import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

public class MyDriver {

	public static void main(String[] args) throws Exception {
		String InputFiles=args[0];
		String OutputDir=args[1];

		Configuration myCon=new Configuration();
		JobConf conf = new JobConf(myCon, MyDriver.class);

		conf.setOutputKeyClass(Text.class);
		conf.setMapOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);

		conf.setMapperClass(MyMapper.class);
		conf.setReducerClass(MyReducer.class);

		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(MultiFileOutput.class);

		FileInputFormat.setInputPaths(conf,InputFiles);
		FileOutputFormat.setOutputPath(conf,new Path(OutputDir));
		JobClient.runJob(conf);

	}
}