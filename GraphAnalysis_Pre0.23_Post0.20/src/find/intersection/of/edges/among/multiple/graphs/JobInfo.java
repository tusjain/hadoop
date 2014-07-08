package find.intersection.of.edges.among.multiple.graphs;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class JobInfo {

	public Class<? extends Reducer> getCombinerClass() {
		return IntersectReducer.class;
	}

	public Class<?> getJarByClass() {
		return IntersectDriver.class;
	}

	public Class<? extends Mapper> getMapperClass() {
		return IntersectMapper.class;
	}

	public Class<?> getOutputKeyClass() {
		return Text.class;
	}

	public Class<?> getOutputValueClass() {
		return Text.class;
	}

	public Class<? extends Reducer> getReducerClass() {
		return IntersectReducer.class;
	}
}