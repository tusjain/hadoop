package iterative.mapreduce.and.counter;

import iterative.mapreduce.and.counter.SearchMapperSSSP;
import iterative.mapreduce.and.counter.SearchReducerSSSP;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class JobInfo {
	public Class<? extends Reducer> getCombinerClass() {
		return null;
	}

	public Class<?> getJarByClass() {
		return SearchSSSPDriver.class;
	}

	public Class<? extends Mapper> getMapperClass() {
		return SearchMapperSSSP.class;
	}

	public Class<?> getOutputKeyClass() {
		return Text.class;
	}

	public Class<?> getOutputValueClass() {
		return Text.class;
	}

	public Class<? extends Reducer> getReducerClass() {
		return SearchReducerSSSP.class;
	}
}