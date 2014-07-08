package com.kashit.traffic;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class AveragerMapper extends Mapper<LongWritable, Text, Text, AverageWritable> {
  
  private AverageWritable outAverage = new AverageWritable();
  private Text id = new Text();
  
  @Override
  public void map(LongWritable key, Text line, Context context)
      throws InterruptedException, IOException {
    String[] tokens = line.toString().split(",");
    if (tokens.length < 10) {
      context.getCounter("Averager Counters", "Blank lines").increment(1);
      return;
    }
    String dateTime = tokens[0];
    String stationId = tokens[1];
    String trafficCount = tokens[9];

    if (trafficCount.length() > 0) {
      id.set(stationId + "_" + TimeUtil.toTimeOfWeek(dateTime));
      if (trafficCount.matches("[0-9]+")) {
        outAverage.set(1, Integer.parseInt(trafficCount));
      } else {
        context.getCounter("Averager Counters", "Missing vehicle flows").increment(1);
      }
      
      context.write(id, outAverage);
    }
  }
}