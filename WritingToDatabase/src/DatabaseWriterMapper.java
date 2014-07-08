import java.io.IOException;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;

public class DatabaseWriterMapper extends Mapper<LongWritable, DBInputWritable, Text, IntWritable>
{
   private IntWritable one = new IntWritable(1);

   protected void map(LongWritable id, DBInputWritable value, Context ctx)
   {
     try
     {
        String[] keys = value.getName().split(" ");

        for(String key : keys)
        {
           ctx.write(new Text(key),one);
        }
     } catch(IOException e)
     {
        e.printStackTrace();
     } catch(InterruptedException e)
     {
        e.printStackTrace();
     }
   }
}