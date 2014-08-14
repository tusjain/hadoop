package com.kashit.hive.udf.simpleAPI;

import org.junit.*;
import org.apache.hadoop.io.*;

public class SimpleUDFExampleTest {

	  @Test
	  public void testUDF() {
	    SimpleUDFExample example = new SimpleUDFExample();
	    Assert.assertEquals("Hello world", example.evaluate(new Text("world")).toString());
	  }

	@Test
	public void testUDFNullCheck() {  
		SimpleUDFExample example = new SimpleUDFExample();  
		Assert.assertNull(example.evaluate(null));}
	}