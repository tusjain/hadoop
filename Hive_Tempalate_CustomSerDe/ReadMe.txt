This template class uses Hive serde2 API (org.apache.hadoop.hive.serde2). 
This API should be used in favor of the older serde API, which has been deprecated:

The initialize() method is called only once and gathers some commonly-used pieces of information
from the table properties, such as the column names and types. Using the type info of the row, 
you can instantiate an ObjectInspector for the row (ObjectInspectors are Hive objects that are 
used to describe and examine complex type hierarchies.) The two other important methods are 
serialize() and deserialize(), which do the namesake work of the SerDe.

In a SerDe, the serialize() method takes a Java object representing a row of data, and converts
that object into a serialized representation of the row. The serialized class is determined by
the return type of getSerializedClass().

The SerDe interface is extremely powerful for dealing with data with a complex schema. By 
utilizing SerDes, any dataset can be made queryable through Hive.