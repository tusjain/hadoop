package custom.partitioner;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class JobInfo {
	public Class<? extends Reducer> getCombinerClass() {
		return null;
	}

	public Class<?> getJarByClass() {
		return PartitionerDriver.class;
	}

	public Class<? extends Mapper> getMapperClass() {
		return PartitionMapper.class;
	}

	public Class<?> getOutputKeyClass() {
		return Text.class;
	}

	public Class<?> getOutputValueClass() {
		return Text.class;
	}

	public Class<? extends Reducer> getReducerClass() {
		return ParitionReducer.class;
	}
}