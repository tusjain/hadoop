package custom.combiner;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import HadoopGTK.EnumerateTriads.TriadCombiner;
import HadoopGTK.EnumerateTriads.TriadMapper;
import HadoopGTK.EnumerateTriads.TriadReducer;

public class JobInfo {
	public Class<? extends Reducer> getReducerClass() {
		return TriadReducer.class;
	}

	
	public Class<?> getOutputValueClass() {
		return Text.class;
	}

	
	public Class<?> getOutputKeyClass() {
		return Text.class;
	}

	
	public Class<? extends Mapper> getMapperClass() {
		return TriadMapper.class;
	}

	
	public Class<?> getJarByClass() {
		return TriadDriver.class;
	}

	
	public Class<? extends Reducer> getCombinerClass() {
		return TriadCombiner.class;
	}
}