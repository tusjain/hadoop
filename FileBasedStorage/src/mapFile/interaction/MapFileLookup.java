/****************************************
 * Java program to lookup data in map file
 * **************************************/
package mapFile.interaction;

import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.conf.Configuration;

public class MapFileLookup {

  /*
	This program looks up a map file for a certain key and returns the associated value
	The call to this program is:
	Parameters:
	param 1: Path to map file 
	param 2: Key for which we want to get the value from the map file
	Return: The value for the key
	Return type: Text
	Sample call: hadoop jar MapFileLookup.jar MapFileLookup <map-file-directory> <key> 
	 */
	@SuppressWarnings("deprecation")
	public static Text main(String[] args) throws IOException {

		Configuration conf = new Configuration();
		FileSystem fs = null;
  	Text txtKey = new Text(args[1]);
		Text txtValue = new Text();
		MapFile.Reader reader = null;

		try {
			fs = FileSystem.get(conf);

			try {
				reader = new MapFile.Reader(fs, args[0].toString(), conf);
				reader.get(txtKey, txtValue);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
                        if(reader != null)
				reader.close();
  		}
		System.out.println("The key is " + txtKey.toString()
				+ " and the value is " + txtValue.toString());
		return txtValue;
	}
}
