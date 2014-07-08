package mapreduceComponents;

import inputs.TextPair;
import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class BigramCountMapper extends Mapper<LongWritable, Text, TextPair, IntWritable>{

    private static Text lastWord = null;
    private static TextPair textPair = new TextPair();
    private static Text wordText = new Text();
    private static IntWritable one = new IntWritable(1);

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
        String line = value.toString();
        line = line.replace(",", "");
        line = line.replace(".", "");
         for(String word: line.split("\\W+")){
            if(lastWord == null){
                lastWord = new Text(word);
            }
            else{
                wordText.set(word);
                textPair.set(lastWord, wordText);
                context.write(textPair, one);
                lastWord.set(wordText.toString());
            }
        }
    }
}