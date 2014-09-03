Custom Partitioner

This example illustrates how to use customized partitioner in a MapReduce program. The partitioning phase takes place after the map phase and before the reduce phase. The number of partitions is equal to the number of reducers. The data gets partitioned across the reducers according to the partitioning function[1] . The difference between a partitioner and a combiner is that the partitioner divides the data according to the number of reducers so that all the data in a single partition gets executed by a single reducer. However, the combiner functions similar to the reducer and processes the data in each partition. The combiner is an optimization to the reducer. The default partitioning function is the hash partitioning function where the hashing is done on the key. However it might be useful to partition the data according to some other function of the key or the value.

For this example we do not consider a graph problem. Let's consider the data that has input in the following format:

Input Format:
name<tab>age<tab>gender<tab>score

We will use custom partitioning in MapReduce program to find the maximum scorer in each gender and three age categories: less than 20, 20 to 50, greater than 50.

Input

Alice<tab>23<tab>female<tab>45
Bob<tab>34<tab>male<tab>89
Chris<tab>67<tab>male<tab>97
Kristine<tab>38<tab>female<tab>53
Connor<tab>25<tab>male<tab>27
Daniel<tab>78<tab>male<tab>95
James<tab>34<tab>male<tab>79
Alex<tab>52<tab>male<tab>69
Nancy<tab>7<tab>female<tab>98
Adam<tab>9<tab>male<tab>37
Jacob<tab>7<tab>male<tab>23
Mary<tab>6<tab>female<tab>93
Clara<tab>87<tab>female<tab>72
Monica<tab>56<tab>female<tab>92


Output:

Partition - 0: (this partition contains the maximum scorers for each gender whose age is less than 20)
Nancy<tab>age- 7<tab>female<tab>score-98
Adam<tab>age- 9<tab>male<tab>score-37

Partition - 1: (this partition contains the maximum scorers for each gender whose age is between 20 and 50)

Kristine<tab>age- 38<tab>female<tab>score-53
Bob<tab>age- 34<tab>male<tab>score-89

Partition - 2: (this partition contains the maximum scorers for each gender whose age is greater than 50)

Monica<tab>age- 56<tab>female<tab>score-92
Chris<tab>age- 67<tab>male<tab>score-97