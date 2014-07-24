import java.io.IOException;
import java.math.BigDecimal;
import java.util.Scanner;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;

public class SalaryCalculatorMapper extends Mapper<LongWritable, Text, Text, Text>{
	//Variables for Map Reduce
	private final static Text dummyVal = new Text("");
	private Text word = new Text();

	//Variables for business calculations
	private String employeeId,employeeName,locationId,gradeId;
	private Double monthlyPay,daysWorked,monthlyHolidays,currentMonthPay;

	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
		/* extracting each line and sending it for processing to process method  and 
             retrieving the result back for sending to output collector*/
		String output=processRecord(value.toString());
		//casting the String to Text
		word.set(output);
		//Sending key value to output collector
		context.write(word, dummyVal);
	}

	public String processRecord(String record){
		StringBuilder outputRecord=new StringBuilder("");

		try {
			Scanner scanner = new Scanner(record);
			//setting the delimiter used in input file
			scanner.useDelimiter("~");
			if ( scanner.hasNext()){
				employeeId = scanner.next();
				employeeName=scanner.next();
				locationId=scanner.next();
				gradeId=scanner.next();
				monthlyPay=Double.parseDouble(scanner.next());
				daysWorked=Double.parseDouble(scanner.next());
				monthlyHolidays=Double.parseDouble(scanner.next());

				//Initializing the calculated salary as 0
				currentMonthPay=0.0;

				//salary computations
				if((daysWorked+monthlyHolidays)!=30){
					currentMonthPay=(monthlyPay/30)*(daysWorked+monthlyHolidays);
				}
				else{
					currentMonthPay=monthlyPay;
				}
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