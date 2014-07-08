import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

public class MultiFileOutput extends MultipleTextOutputFormat<Text, Text> {
	protected String generateFileNameForKeyValue(Text key, Text value,String name) {
		return key.toString();
	}
}