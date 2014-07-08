import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBInputFormat;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;

public class DatabaseWriterDriver
{
   public static void main(String[] args) throws Exception
   {
     Configuration conf = new Configuration();
     DBConfiguration.configureDB(conf,
     "com.mysql.jdbc.Driver",   // driver class
     "jdbc:mysql://localhost:3306/testDb", // db url
     "root",    // user name
     "hadoop123"); //password

     Job job = new Job(conf);
     job.setJarByClass(DatabaseWriterDriver.class);
     job.setMapperClass(DatabaseWriterMapper.class);
     job.setReducerClass(DatabaseWriterReducer.class);
     job.setMapOutputKeyClass(Text.class);
     job.setMapOutputValueClass(IntWritable.class);
     job.setOutputKeyClass(DBOutputWritable.class);
     job.setOutputValueClass(NullWritable.class);
     job.setInputFormatClass(DBInputFormat.class);
     job.setOutputFormatClass(DBOutputFormat.class);

     DBInputFormat.setInput(
     job,
     DBInputWritable.class,
     "studentinfo",   //input table name
     null,
     null,
     new String[] { "id", "name" }  // table columns
     );

     DBOutputFormat.setOutput(
     job,
     "output",    // output table name
     new String[] { "name", "count" }   //table columns
     );

     System.exit(job.waitForCompletion(true) ? 0 : 1);
   }
}