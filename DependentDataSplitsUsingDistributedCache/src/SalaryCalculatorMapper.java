import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class SalaryCalculatorMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, NullWritable>{
	//Variables for Map Reduce
	private Text word = new Text();

	//Variables for business calculations
	private String employeeId,employeeName,locationId,gradeId;
	private Double monthlyPay,daysWorked,monthlyHolidays,currentMonthPay,locationAllowance,gradeBonus;

	//data structures for storing location and grade details
	private static Map<String, String> locationMap = new HashMap<String, String>();
	private static Map<String, String> gradeMap = new HashMap<String, String>();

	//variables for processing reference files
	private String locId,locName,locAllowance,grId,grade,grBonus;

	public void configure(JobConf job){
		try {
			processFile(new File("location.txt"));
			processFile(new File("grade.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void processFile(File fFile) throws FileNotFoundException {

		Scanner scanner = new Scanner(fFile);
		try {
			// first use a Scanner to get each line
			while (scanner.hasNextLine()) {
				if(fFile.getName().equals("location.txt"))
					processLineLocation(scanner.nextLine());
				else if(fFile.getName().equals("grade.txt"))
					processLineGrade(scanner.nextLine());
			}
		} finally {
			// ensure the underlying stream is always closed
			scanner.close();
		}
	}

	public void processLineLocation(String aLine) {
		// use a second Scanner to parse the content of each line

		try {
			Scanner scanner = new Scanner(aLine);
			scanner.useDelimiter(",");
			if (scanner.hasNext()) {
				locId = scanner.next();
				locName = scanner.next();
				locAllowance = scanner.next();
				//we dont need location name for our computations hence not including the same in map
				locationMap.put(locId.trim(),locAllowance.trim());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void processLineGrade(String aLine) {
		// use a second Scanner to parse the content of each line
		try {
			Scanner scanner = new Scanner(aLine);
			scanner.useDelimiter(",");
			if (scanner.hasNext()) {
				grId = scanner.next();
				grade = scanner.next();
				grBonus = scanner.next();
				//we dont need grade for our computations hence not including the same in map
				gradeMap.put(grId.trim(),grBonus.trim());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void map(LongWritable key, Text value, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException{
		/* extracting each line and sending it for processing to process method  and
             retrieving the result back for sending to output collector*/
		String outputText=processRecord(value.toString());

		//casting the String to Text
		word.set(outputText);

		//Sending key value to output collector
		output.collect(word, NullWritable.get());
	}

	public String processRecord(String record){
		StringBuilder outputRecord=new StringBuilder("");

		try {
			Scanner scanner = new Scanner(record);
			//setting the delimiter used in input file
			scanner.useDelimiter("~");
			if ( scanner.hasNext() ){
				employeeId = scanner.next();
				employeeName=scanner.next();
				locationId=scanner.next();
				gradeId=scanner.next();
				monthlyPay=Double.parseDouble(scanner.next());
				daysWorked=Double.parseDouble(scanner.next());
				monthlyHolidays=Double.parseDouble(scanner.next());

				//Initializing the calculated salary as 0
				currentMonthPay=0.0;

				//Computing location allowance and grade bonus
				locationAllowance = Double.parseDouble(locationMap.get(locationId.trim()));
				gradeBonus = Double.parseDouble(gradeMap.get(gradeId.trim()));

				//monthly salary computations
				if((daysWorked+monthlyHolidays)!=30){
					currentMonthPay=(monthlyPay/30)*(daysWorked+monthlyHolidays);
				}else{
					currentMonthPay=monthlyPay;
				}
				//total salary for a month
				currentMonthPay = currentMonthPay+(locationAllowance/12) +(gradeBonus/12);

				//rounding using Big Decimal
				BigDecimal bd = new BigDecimal(currentMonthPay);
				bd = bd.setScale(2,BigDecimal.ROUND_UP);//2 -  decimal places
				currentMonthPay = bd.doubleValue();

				//get output in the format Employee Id~Employee Name~Monthly Pay~Days Worked~Calculated Salary
				outputRecord.append(employeeId);
				outputRecord.append("~");
				outputRecord.append(employeeName);
				outputRecord.append("~");
				outputRecord.append(monthlyPay.toString());
				outputRecord.append("~");
				outputRecord.append(daysWorked.toString());
				outputRecord.append("~");
				outputRecord.append(currentMonthPay.toString());
			} 
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return outputRecord.toString();
	}
}