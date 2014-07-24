import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class SmsReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
	//Variables to aid the join process
	private String customerName,deliveryReport;
	/*Map to store Delivery Codes and Messages
       Key being the status code and vale being the status message*/
	private static Map<String,String> DeliveryCodesMap= new HashMap<String,String>();

	public void configure(JobConf job) {
		//To load the Delivery Codes and Messages into a hash map
		loadDeliveryStatusCodes();

	}

	public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException{
		while (values.hasNext()){
			String currValue = values.next().toString();
			String valueSplitted[] = currValue.split("~");
			/*identifying the record source that corresponds to a cell number
             and parses the values accordingly*/
			if(valueSplitted[0].equals("CD")){
				customerName=valueSplitted[1].trim();
			}else if(valueSplitted[0].equals("DR")){
				//getting the delivery code and using the same to obtain the Message
				deliveryReport = DeliveryCodesMap.get(valueSplitted[1].trim());
			}
		}

		//pump final output to file
		if(customerName!=null && deliveryReport!=null){
			output.collect(new Text(customerName), new Text(deliveryReport));
		}else if(customerName==null){
			output.collect(new Text("customerName"), new Text(deliveryReport));
		}else if(deliveryReport==null){
			output.collect(new Text(customerName), new Text("deliveryReport"));
		}
	}

	//To load the Delivery Codes and Messages into a hash map
	private void loadDeliveryStatusCodes(){
		String strRead;
		try {
			//read file from Distributed Cache
			BufferedReader reader = new BufferedReader(new FileReader("DeliveryStatusCodes.txt"));
			while ((strRead=reader.readLine() ) != null){
				String splitarray[] = strRead.split(",");
				//parse record and load into HahMap
				DeliveryCodesMap.put(splitarray[0].trim(), splitarray[1].trim());
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch( IOException e ) {
			e.printStackTrace();
		}
	}
}