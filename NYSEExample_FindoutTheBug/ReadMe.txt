Input Datasets:
++++++++++++++++++++

The NYSE Daily Prices File has the following colums separated by commas(CSV File):
-    exchange, stock_symbol, date, stock_price_open, stock_price_high, stock_price_low, stock_price_close, stock_volume, stock_price_adj_close
Sample Record: NYSE,AEA,2010-02-08,4.42,4.42,4.21,4.24,205500,4.24

The NYSE Daily Dividends File has the following columns separated by commas(CSV File):
-    exchange, stock_symbol, date, dividends
Sample record: NYSE,AIT,2009-11-12,0.15

The NYSE Companies file lists the stock symbol along with its Company name. This is a tab separated file with the following columns:
-    symbol, company_name
Sample Record: ACAS AMERICAN CAPITAL LTD

You can download these input data files from https://dl.dropboxusercontent.com/u/27032474/NYSE.tar.gz and put them on Your hdfs cluster using the put or copyFromLocal command.

Requirements:
++++++++++++++++++++

Now lets consider generating reports split by years which shows the below fields:
stock_symbol, company_name, max_stock_price for the year for the company, min_stock_price for the year for the company, average dividends for the year for the company. Reports for different years have to go to different output files.

Design Considerations:
+++++++++++++++++++++++

Point 1: As in most MapReduce Program cases, our mapper(s) will do the filtration and transformation of the input data and our reducer will do the aggregation and generation of desired output.

Point 2: Since I use data from multiple files, I have to join the input files to generate the desired report. Since the dividends and Prices files are huge, I am going to use MultipleInputs Class on them and create two Mappers one for each file. The two mappers are going to generate similar key-value pairs and there will be a identifier in the values which will tell the reducer about which file the key-value pair has come from. Thus this program is going to be a classic example of joining input files based on some keys.

Point 3: Since I am dealing with multiple inputs and outputs, it would be easier implementing the MapReduce job using the deprecated(old) MapRed API.

Point 4: The NYSE Companies File is not a huge one and thus qualifies to be used as a Distributed Cache File to map the company_symbol to the Company_Name.

Point 5: I create two Customized Hadoop Datatypes(Objects) to be passed from the mappers to the reducer as key-value pairs - NYSEWritable to be used as the value and NYSESymbolYearWritable to be used as the Key. Since its used as the key, NYSESymbolYearWritable has to implement WritableComparable. NYSEWritable implements Writable interface. Hence You can learn creating and using Hadoop User Defined Data Types.

Point 6: I use the MultipleTextOutputFormat class to split the reducer output to files in different directories based on the Year. This class is defined in job.setOutputFormat. This is another important feature of MapReduce Frame Work that can be learnt from this example.