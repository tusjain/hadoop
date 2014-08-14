Considering a source data set as follow made of 4 columns (customer id, timestamp, meter value, meter state):

195100,1199145600,607527807,B
185100,1199145600,775031942,A
195100,1199156400,607532682,B
185100,1199156400,775032584,A
195100,1199167200,607535485,B
185100,1199167200,775033200,A
195100,1199178000,582924326,C
185100,1199178000,775034241,A
195100,1199188800,582927007,C
185100,1199188800,775035698,A
195100,1199199600,582929212,C
185100,1199199600,775036891,A
195100,1199210400,582932070,C
185100,1199210400,775038268,A
195100,1199221200,582935353,B
185100,1199221200,775039703,A

The CSV source is imported into Hive with the next statements:

DROP TABLE source;
CREATE TABLE source (
    customer INT,
    emission INT,
    value INT,
    state STRING
)
ROW FORMAT DELIMITED
    FIELDS TERMINATED BY ','
    LINES TERMINATED BY '\n'
STORED AS TEXTFILE;
LOAD DATA LOCAL INPATH './sample/data.csv' OVERWRITE INTO TABLE source;

Now we can declare our two UDAF:

ADD JAR ./target/adaltas-hive-udf-0.0.1-SNAPSHOT.jar;
CREATE TEMPORARY FUNCTION to_map as 'com.adaltas.UDAFToMap';
CREATE TEMPORARY FUNCTION to_ordered_map as 'com.adaltas.UDAFToOrderedMap';

And we can finally used them with the following queries:

 # HashMap implementation
SELECT
    `customer`, to_map(from_unixtime(emission), array(value,state))
FROM `source`
GROUP BY `customer`;
 # Ordered HashMap implementation
SELECT
    `customer`, to_ordered_map(from_unixtime(emission), array(value,state))
FROM `source`
GROUP BY `customer`;

The output of the last select statement looks like:

185100  {
    "2008-01-01 01:00:00":["775031942","A"],
    "2008-01-01 04:00:00":["775032584","A"],
    "2008-01-01 07:00:00":["775033200","A"],
    "2008-01-01 10:00:00":["775034241","A"],
    "2008-01-01 13:00:00":["775035698","A"],
    "2008-01-01 16:00:00":["775036891","A"],
    "2008-01-01 19:00:00":["775038268","A"],
    "2008-01-01 22:00:00":["775039703","A"] }
195100  {
    "2008-01-01 01:00:00":["607527807","B"],
    "2008-01-01 04:00:00":["607532682","B"],
    "2008-01-01 07:00:00":["607535485","B"],
    "2008-01-01 10:00:00":["582924326","C"],
    "2008-01-01 13:00:00":["582927007","C"],
    "2008-01-01 16:00:00":["582929212","C"],
    "2008-01-01 19:00:00":["582932070","C"],
    "2008-01-01 22:00:00":["582935353","B"]}
