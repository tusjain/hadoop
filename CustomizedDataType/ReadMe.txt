There can be use cases where none of the built-in data types matches the requirements or a custom data type
optimized for a  use case may perform better than a Hadoop built-in data type. In such scenarios a 
custom Writable data type can be written by implementing the org.apache.hadoop.io.Writable interface
to define the serialization format of your data type.

The Writable interface-based types can be used as value types in Hadoop MapReduce computations.


In this recipe, we implement a sample Hadoop Writable data type for HTTP server log entries.
For the purpose of this sample, we consider that a log entry consists of the five fields:

 — request host,
 - timestamp,
 - request URL,
 - response size, and
 - http status code.
 
The following is a sample log entry:
192.168.0.2 - - [01/Jul/1995:00:00:01 -0400] "GET /history/apollo/HTTP/1.0" 200 6245
