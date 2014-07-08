package com.kashit.traffic;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class AveragerReducer extends Reducer<Text, AverageWritable, Text, AverageWritable> {
  
  private AverageWritable outAverage = new AverageWritable();
  
  @Override
  public void reduce(Text key, Iterable<AverageWritable> averages, Context context)
      throws InterruptedException, IOException {
    double sum = 0.0;
    int numElements = 0;
    for (AverageWritable average : averages) {
      sum += average.getAverage() * average.getNumElements();
      numElements += average.getNumElements();
    }
    double average = sum / numElements;
    
    outAverage.set(numElements, average);
    context.write(key, outAverage);
  }
}