package wordCount;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.NullOutputFormat;

public class WordCount {

	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Duo> {
		private Text word = new Text();

		public void map(LongWritable key, Text value, OutputCollector<Text, Duo> output, Reporter reporter) throws IOException {
			String line = value.toString();
			StringTokenizer tokenizer = new StringTokenizer(line);
			while (tokenizer.hasMoreTokens()) {
				word.set(tokenizer.nextToken());
				output.collect(word, new Duo("kumar","kumar"));
			}
		}
	}

	public static class Reduce extends MapReduceBase implements Reducer<Text, Duo, Text, IntWritable> {

		public void reduce(Text key, Iterator<Duo> values,
				OutputCollector<Text, IntWritable> output, Reporter reporter) throws
				IOException {
			Configuration conf = new Configuration();
			FileSystem fs = FileSystem.get(conf);
			Path subjectFile = new Path(key.toString());
			FSDataOutputStream out = fs.create(subjectFile);
			while (values.hasNext()) {
				out.writeChars(key.toString());
				Duo d = values.next();
				out.writeChars(d.getProperty());
				out.writeChars(d.getObject());
			}
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("1");
		JobConf conf = new JobConf(WordCount.class);
		conf.setJobName("wordcount");

		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Duo.class);

		conf.setMapperClass(Map.class);
		conf.setCombinerClass(Reduce.class);
		conf.setReducerClass(Reduce.class);

		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(NullOutputFormat.class);

		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));
		System.out.println("2");
		JobClient.runJob(conf);
	}
}