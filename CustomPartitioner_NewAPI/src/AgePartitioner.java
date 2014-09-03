/*
 * Lines 6-33 implement a customized partitioner called the AgePartitioner that extends the Partitioner class. 
 * It overrides the getPartition function (lines 9-32), which has three parameters. The key and value are the
 * intermediate key and value produced by the map function. The numReduceTasks is the number of reducers used 
 * in the MapReduce program and it is specified in the driver program. Here we parse the value and get the age
 * information in lines 12-13. Then we assign them to the partition 0, 1, or 2, depending on the age categories
 * [lines 20-30]. Lines 16-17 return partition 0 in case the number of reducers is set to 0, to avoid divide by
 * zero exception. It is possible to have empty partitions with no data. We do the assigned partition number 
 * modulo numReduceTasks to avoid illegal partitions if the system has a lesser number of possible reducers 
 * than the assigned number.
 */

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

//AgePartitioner is a custom Partitioner to partition the data according to age.
    //The age is a part of the value from the input file.
    //The data is partitioned based on the range of the age.
    //In this example, there are 3 partitions, the first partition contains the information where the age is less than 20
    //The second partition contains data with age ranging between 20 and 50 and the third partition contains data where the age is >50.
    public class AgePartitioner extends Partitioner<Text, Text> {
 
        @Override
        public int getPartition(Text key, Text value, int numReduceTasks) {
 
            String [] nameAgeScore = value.toString().split("\t");
            String age = nameAgeScore[1];
            int ageInt = Integer.parseInt(age);
           
            //this is done to avoid performing mod with 0
            if(numReduceTasks == 0)
                return 0;
 
            //if the age is <20, assign partition 0
            if(ageInt <=20){               
                return 0;
            }
            //else if the age is between 20 and 50, assign partition 1
            if(ageInt >20 && ageInt <=50){
               
                return 1 % numReduceTasks;
            }
            //otherwise assign partition 2
            else
                return 2 % numReduceTasks;
           
        }
    }
