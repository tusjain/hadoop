package com.kashit.traffic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class AverageWritable implements Writable {

  private int numElements;
  private double average;
 
  public AverageWritable() {}
  
  public void set(int numElements, double average) {
    this.numElements = numElements;
    this.average = average;
  }
  
  public int getNumElements() {
    return numElements;
  }
  
  public double getAverage() {
    return average;
  }
  
  @Override
  public void readFields(DataInput input) throws IOException {
    numElements = input.readInt();
    average = input.readDouble();
  }

  @Override
  public void write(DataOutput output) throws IOException {
    output.writeInt(numElements);
    output.writeDouble(average);
  }
  
  public String toString() {
    return "(" + numElements + ", " + average + ")";
  }
  
  @Override
  public boolean equals(Object o) {
    AverageWritable other = (AverageWritable)o;
    return other.numElements == numElements && other.average - average < .0001;
  }
  
  @Override
  public int hashCode() {
    return numElements * 31 + (int)average;
  }
}