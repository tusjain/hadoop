Script 1: To find out stocks whose name starting with letter  “A”

STOCK_A = LOAD 'nyse/NYSE_daily_prices_A.csv' using PigStorage(','); 
DESCRIBE STOCK_A; 

Script 2:
Let’s use the above code but this time with a schema. Modify line 1 of your 
script and add the following “AS”" clause to define a schema for the daily stock price data.

STOCK_A = LOAD 'NYSE_daily_prices_A.csv' using PigStorage(',') AS (exchange:chararray, symbol:chararray, date:chararray, open:float, high:float, low:float, close:float, volume:int, adj_close:float);
DESCRIBE STOCK_A;

Script 3:
You can define a new relation based on an existing one. For example, define the following B relation, which is a collection of 100 entries (arbitrarily selected) from the STOCK_A relation.

STOCK_A = LOAD 'NYSE_daily_prices_A.csv' using PigStorage(',') AS (exchange:chararray, symbol:chararray, date:chararray, open:float, high:float, low:float, close:float, volume:int, adj_close:float);
DESCRIBE STOCK_A;
B = LIMIT STOCK_A 100;
DESCRIBE B;

Script 4:

View the data

STOCK_A = LOAD 'NYSE_daily_prices_A.csv' using PigStorage(',') AS (exchange:chararray, symbol:chararray, date:chararray, open:float, high:float, low:float, close:float, volume:int, adj_close:float);
DESCRIBE STOCK_A;
B = LIMIT STOCK_A 100;
DESCRIBE B;
DUMP B;

Script 5:

Delete the DESCRIBE STOCK_A, DESCRIBE B, and DUMP B commands from your Pig script; you will no longer need those.
One of the key uses of Pig is data transformation. You can define a new relation based on the fields of an existing
relation using the FOREACH command. Define a new relation C, which will contain only the symbol, date and close fields from relation B.

STOCK_A = LOAD 'NYSE_daily_prices_A.csv' using PigStorage(',') AS (exchange:chararray, symbol:chararray, date:chararray, open:float, high:float, low:float, close:float, volume:int, adj_close:float);
B = LIMIT STOCK_A 100;
C = FOREACH B GENERATE symbol, date, close;
DESCRIBE C;


Script 6:
In this step, you will use the STORE command to output a relation into a new file in HDFS.

STOCK_A = LOAD 'NYSE_daily_prices_A.csv' using PigStorage(',') AS (exchange:chararray, symbol:chararray, date:chararray, open:float, high:float, low:float, close:float, volume:int, adj_close:float);
B = LIMIT STOCK_A 100;
C = FOREACH B GENERATE symbol, date, close;
DESCRIBE C;
STORE C INTO 'output/C'; 


Script 7:

In this step, you will perform a join on two NYSE data sets: the daily prices and the dividend prices.
Dividends prices are shown for the quarter, while stock prices are represented on a daily basis.
You have already defined a relation for the stocks named STOCK_A. Create a new Pig script named “Pig-Join”.
Then define a new relation named DIV_A that represents the dividends for stocks that start with an “A”, then
join A and B by both the symbol and date and describe the schema of the new relation C.

STOCK_A = LOAD 'NYSE_daily_prices_A.csv' using PigStorage(',') AS (exchange:chararray, symbol:chararray, date:chararray, open:float, high:float, low:float, close:float, volume:int, adj_close:float);
DIV_A = LOAD 'NYSE_dividends_A.csv' using PigStorage(',') AS (exchange:chararray, symbol:chararray, date:chararray, dividend:float);
C = JOIN STOCK_A BY (symbol, date), DIV_A BY (symbol, date);
DESCRIBE C;

Script 8:

Use the ORDER BY command to sort a relation by one or more of its fields. Create a new Pig script named “Pig-sort” and
enter the following commands to sort the dividends by symbol then date in ascending order.

DIV_A = LOAD 'NYSE_dividends_A.csv' using PigStorage(',') AS (exchange:chararray, symbol:chararray, date:chararray, dividend:float);
B = ORDER DIV_A BY symbol, date asc;
DUMP B; 

Script 9:

The GROUP command allows you to group a relation by one of its fields. Create a new Pig script named “Pig-group”. 
Then, enter the following commands, which group the DIV_A relation by the dividend price for the “APP” stock.

DIV_A = LOAD 'NYSE_dividends_A.csv' using PigStorage(',') AS (exchange:chararray, symbol:chararray, date:chararray, dividend:float);
B = FILTER DIV_A BY symbol=='APP';
C = GROUP B BY dividend;
DESCRIBE C;
DUMP C;




















