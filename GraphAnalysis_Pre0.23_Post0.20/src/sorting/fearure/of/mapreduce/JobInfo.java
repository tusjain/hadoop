package sorting.fearure.of.mapreduce;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class JobInfo {
	public Class<? extends Reducer> getCombinerClass() {
		
		return null;
	}

	
	public Class<?> getJarByClass() {
		return MSTDriver.class;
	}

	
	public Class<? extends Mapper> getMapperClass() {
		return MSTMapper.class;
	}

	
	public Class<?> getOutputKeyClass() {
		return IntWritable.class;
	}

	
	public Class<?> getOutputValueClass() {
		return Text.class;
	}

	
	public Class<? extends Reducer> getReducerClass() {
		return MSTReducer.class;
	}
}