Problem Statement:
----------------------
Lets write a simple Java UDF which takes input as Tuple of two DataBag and check whether 
second databag(set) is subset of first databag(set).
For example, Assume you have been given tuple of two databags. Each DataBag contains 
elements(tuples) as number.

Input:
Databag1 : {(10),(4),(21),(9),(50)}
Databag2 : {(9),(4),(50)}
Output:
True

Then function should return true as Databag2 is subset of Databag1.
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


Lets test our UDF to find whether given set is subset of other set or not.

-- Register jar which contains UDF.
register '/home/hadoop/udf.jar';
 
-- Define function for use.
define isSubset IsSubSet();
 
-- lets assume we have dataset as following :
 dump datset;
--({(10),(4),(21),(9),(50)},{(9),(4),(50)})
--({(50),(78),(45),(7),(4)},{(7),(45),(50)})
--({(1),(2),(3),(4),(5)},{(4),(3),(50)})
 
-- lets check subset function
result = foreach dataset generate $0,$1,isSubset($0,$1);
 
dump result;
--({(10),(4),(21),(9),(50)},{(9),(4),(50)},true)
--({(50),(78),(45),(7),(4)},{(7),(45),(50)},false)
--({(1),(2),(3),(4),(5)},{(4),(3),(50)},false)