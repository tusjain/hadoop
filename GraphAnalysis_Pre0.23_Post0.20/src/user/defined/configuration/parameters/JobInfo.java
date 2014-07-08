package user.defined.configuration.parameters;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class JobInfo {
	public Class<? extends Reducer> getCombinerClass() {
		return null;
	}

	public Class<?> getJarByClass() {
		return ReachabilityDriver.class;
	}

	public Class<? extends Mapper> getMapperClass() {
		return ReachabilityMapper.class;
	}

	public Class<?> getOutputKeyClass() {
		return Text.class;
	}

	public Class<?> getOutputValueClass() {
		return Text.class;
	}

	public Class<? extends Reducer> getReducerClass() {
		return ReachabilityReducer.class;
	}
}