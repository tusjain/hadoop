import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.lib.MultipleInputs;
import org.apache.hadoop.mapred.lib.MultipleTextOutputFormat;

public class NYSEYearlyAnalysis {

 /*
  * This mapper processes the NYSE Prices file. Refer to Point 2 in Design considerations
  */

 public static class PricesMapper extends MapReduceBase implements
   Mapper<LongWritable, Text, NYSESymbolYearWritable, NYSEWritable> {
  NYSESymbolYearWritable mapOutKey = new NYSESymbolYearWritable();
  NYSEWritable mapOutValue = new NYSEWritable();

  @SuppressWarnings("deprecation")
  @Override
  public void map(
    LongWritable mapInKey,
    Text mapInValue,
    OutputCollector<NYSESymbolYearWritable, NYSEWritable> collector,
    Reporter reporter) throws IOException {
   String[] mapInFieldsArray = mapInValue.toString().split(",");
   DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
   if (mapInFieldsArray.length == 9 & mapInFieldsArray[0].trim().equals("exchange") == false) {
    /* Set the Map Output Key Variables */
    mapOutKey.setStock_symbol(mapInFieldsArray[1]);
    try {
     /*
      * Note that the getYear method returns the year subtracting
      * 1900 from the year value. And hence we need to add 1900
      * to output year
      */
     mapOutKey.setStock_year(df.parse(mapInFieldsArray[2]).getYear() + 1900);
    } catch (ParseException e) {
     System.out.println("Not a valid date : " + mapInFieldsArray[2]);
     e.printStackTrace();
    }
    /* Set the Map Output Values Variables */
    mapOutValue.setFile_identifier("prices");
    mapOutValue.setStock_date(mapInFieldsArray[2]);
    mapOutValue.setStock_dividend(0.0);
    mapOutValue.setStock_exchange(mapInFieldsArray[0]);
    mapOutValue.setStock_price_adj_close(Double.valueOf(mapInFieldsArray[8]));
    mapOutValue.setStock_price_close(Double.valueOf(mapInFieldsArray[6]));
    mapOutValue.setStock_price_high(Double.valueOf(mapInFieldsArray[4]));
    mapOutValue.setStock_price_low(Double.valueOf(mapInFieldsArray[5]));
    mapOutValue.setStock_price_open(Double.valueOf(mapInFieldsArray[3]));
    mapOutValue.setStock_symbol(mapInFieldsArray[1]);
    mapOutValue.setStock_volume(Integer.valueOf(mapInFieldsArray[7]).longValue());
    collector.collect(mapOutKey, mapOutValue);
   }
  }
 }

 /*
  * This mapper processes the NYSE Dividend file. Refer to Point 2 in Design
  * Considerations
  */

 public static class DividendMapper extends MapReduceBase implements
   Mapper<LongWritable, Text, NYSESymbolYearWritable, NYSEWritable> {
  NYSESymbolYearWritable mapOutKey = new NYSESymbolYearWritable();
  NYSEWritable mapOutValue = new NYSEWritable();

  @SuppressWarnings("deprecation")
  @Override
  public void map(
    LongWritable mapInKey,
    Text mapInValue,
    OutputCollector<NYSESymbolYearWritable, NYSEWritable> collector,
    Reporter reporter) throws IOException {
   String[] mapInFieldsArray = mapInValue.toString().split(",");
   DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
   if (mapInFieldsArray.length == 4 & mapInFieldsArray[0].trim().equals("exchange") == false) {
    /* Set the Map Output Key Variables */
    mapOutKey.setStock_symbol(mapInFieldsArray[1]);
    try {
     mapOutKey.setStock_year(df.parse(mapInFieldsArray[2]).getYear() + 1900);
    } catch (ParseException e) {
     System.out.println("Not a valid date : "
       + mapInFieldsArray[2]);
     e.printStackTrace();
    }
    /* Set the Map Output Values Variables */
    mapOutValue.setFile_identifier("dividends");
    mapOutValue.setStock_date(mapInFieldsArray[2]);
    mapOutValue.setStock_dividend(Double.valueOf(mapInFieldsArray[3]));
    mapOutValue.setStock_exchange(mapInFieldsArray[0]);
    mapOutValue.setStock_price_adj_close(0.0);
    mapOutValue.setStock_price_close(0.0);
    mapOutValue.setStock_price_high(0.0);
    mapOutValue.setStock_price_low(0.0);
    mapOutValue.setStock_price_open(0.0);
    mapOutValue.setStock_symbol(mapInFieldsArray[1]);
    mapOutValue.setStock_volume(0);
    collector.collect(mapOutKey, mapOutValue);
   }
  }
 }

 public static class MyReducer extends MapReduceBase implements
   Reducer<NYSESymbolYearWritable, NYSEWritable, Text, Text> {
  Text reduceOutKey = new Text();
  Text reduceOutValue = new Text();
  Map<String, String> companiesMap = new HashMap<String, String>();

  /*
   * Read the companies file and load it to a HashMap to be used as
   * Distributed Cache. The HashMap will have the Symbol as the Key and
   * Full company name as Value. See Point 4 in Design Considerations.
   */
  public void loadMap(JobConf job) throws IOException {
   Path companiesFilePath = new Path(
     "/user/hduser/NYSE/companies/NYSESymbolCompany");
   FileSystem fs = FileSystem.get(job);
   FSDataInputStream iStream = fs.open(companiesFilePath);
   InputStreamReader iStreamReader = new InputStreamReader(iStream);
   BufferedReader br = new BufferedReader(iStreamReader);
   String line = "";
   while ((line = br.readLine()) != null) {
    String[] companiesArr = line.split("\t");
    if (companiesArr.length == 2) {
     companiesMap.put(companiesArr[0].trim(), companiesArr[1]);
    }
   }
   /*
    * for (Map.Entry<String, String> mE : companiesMap .entrySet()) {
    * System.out.println("atom : key - " + mE.getKey() + " value - " +
    * mE.getValue()); }
    */
  }

  /*
   * In the old MapRed API, the configure method is run one time at the
   * beginning of Map/Reduce Task. In the New API setup() method takes its
   * place
   */

  public void configure(JobConf job) {
   try {
    loadMap(job);
   } catch (IOException e) {
    System.out.println(" Error calling loadMap");
    e.printStackTrace();
   }
  }

  @Override
  public void reduce(NYSESymbolYearWritable reduceInKey,
    Iterator<NYSEWritable> reduceInValues,
    OutputCollector<Text, Text> collector, Reporter reporter)
    throws IOException {

   double maxStockPrice = 0;
   double minStockPrice = Double.MAX_VALUE;
   double sumDividends = 0;
   NYSEWritable reduceInValue = new NYSEWritable();
   StringBuilder sb = new StringBuilder("");
   int count = 0;
   reduceOutKey.set(reduceInKey.toString());
   while (reduceInValues.hasNext()) {
    reduceInValue = reduceInValues.next();
    if (reduceInValue.getFile_identifier().equals("prices")) {
     maxStockPrice = (maxStockPrice > reduceInValue.getStock_price_high()) ? maxStockPrice : reduceInValue.getStock_price_high();
     minStockPrice = (minStockPrice < reduceInValue.getStock_price_low()) ? minStockPrice : reduceInValue.getStock_price_low();
    } else if (reduceInValue.getFile_identifier().equals("dividends")) {
     sumDividends = sumDividends  + reduceInValue.getStock_dividend();
     count++;
    }
   }
   sb.append("companyName=");
   sb.append(companiesMap.get(reduceInKey.getStock_symbol().trim()));
   sb.append(",maxStockPrice=");
   sb.append(maxStockPrice);
   sb.append(",minStockPrice=");
   sb.append(minStockPrice);
   sb.append(",avgDividend=");
   sb.append(sumDividends / count);
   reduceOutValue.set(sb.toString());
   collector.collect(reduceOutKey, reduceOutValue);
  }
 }

 /*
  * Customized class output file format class to split the output file of
  * reducer based on the Year. Refer to Point 6 in Design Considerations.
  */
 public static class MyMultipleOutputFileFormat extends
   MultipleTextOutputFormat<Text, Text> {
  public String generateFileNameForKeyValue(Text key, Text value,
    String name) {
   String[] keyArr = key.toString().split("=");
   String outfolder = keyArr[2].substring(0, 4);
   return new Path(outfolder, name).toString();
  }
 }

 /* Main Driver Method */
 public static void main(String[] args) throws IOException,
   ClassNotFoundException, InterruptedException {
  Configuration conf = new Configuration();
  JobConf job = new JobConf(conf);

  job.setJobName("NYSE Yearly Analysis");

  job.setJarByClass(NYSEYearlyAnalysis.class);
  // Define Reducer Class. Mapper Classes are defined later for two diff input files.
  job.setReducerClass(MyReducer.class);

  // Output Key-Value Data types
  job.setMapOutputKeyClass(NYSESymbolYearWritable.class);
  job.setMapOutputValueClass(NYSEWritable.class);

  job.setOutputKeyClass(Text.class);
  job.setOutputValueClass(Text.class);

  // Inform Input/Output Formats and Directory Locations

  /*
   * Since there are two mappers here for two different files, You need to
   * define which mapper processes which input file. Refer to Point 2 in
   * Design Considerations.
   */
  MultipleInputs.addInputPath(job, new Path(args[0]),
    TextInputFormat.class, PricesMapper.class);
  MultipleInputs.addInputPath(job, new Path(args[1]),
    TextInputFormat.class, DividendMapper.class);
  FileOutputFormat.setOutputPath(job, new Path(args[2]));
  job.setOutputFormat(MyMultipleOutputFileFormat.class);

  // Inform termination criteria
  JobClient.runJob(job);

 }
}