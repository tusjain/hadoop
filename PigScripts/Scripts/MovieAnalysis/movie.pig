#Load the data

movies = LOAD '/home/hduser/pig/myscripts/movies_data.csv' USING PigStorage(',') as (id,name,year,rating,duration);

#See the data

DUMP movies;

#List the movies that having a rating greater than 4

movies_greater_than_four = FILTER movies BY (float)rating>4.0;
DUMP movies_greater_than_four

# store data file

store movies_greater_than_four into '/user/hduser/movies_greater_than_four'