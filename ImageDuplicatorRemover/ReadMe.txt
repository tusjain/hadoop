The Problem:
---------------
Let’s image a system that download images from the internet using some search string. Some of the images may be identical being the same 
image used in different web sites, so we would like the system to know to filter out the duplicates. And we can expect a huge amount of 
data. That is why we using hadoop for, right?

So the input is a directory full of jpg images and the output should be a list with the unique images.

The Dilemma:
------------

Hadoop is at its best reading from huge data file while distributing the processing on several agents. It using optimizations to have 
the processing unit give priority for its local data as the input for processing. What will happen if we will feed the process with 
huge amount of relatively small files like images?

Apparently it will have a lot of overhead. The HDFS is keeping relatively big amount of overhead for each file in order to be able 
to support features like splitting a file across multiple machines and backup each file with several copies on different machines. 
As for the processing time, It will be affected as well. By default each input file is being send to it own map task, thus, huge amount
of files means a huge amount of map tasks. having lots of map tasks can significantly slow the application by the overhead of each task. 
This can be somewhat relieved by reusing the task’s JVM and by using the MultiFileInputSplit, for more than 1 file in a split.

Using Hadoop Sequence Files
---------------------------

So what should we do in order to deal with huge amount of images? Use hadoop sequence files! Those are map files that inherently can be 
read by map reduce applications – there is an input format especially for sequence files – and are splitable by map reduce, so we can 
have one huge file that will be the input of many map tasks. By using those sequence files we are letting hadoop use its advantages. 
It can split the work into chunks so the processing is parallel, but the chunks are big enough that the process stays efficient.

Since the sequence file are map file the desired format will be that the key will be text and hold the HDFS filename and the value will 
be BytesWritable and will contain the image content of the file.  Remark – for this example’s sake it is even better to store the 
processed binary file (as MD5 text, we will see later), instead of the whole bytes of the file. However, I’m interested in showing how 
to read and write binaries so we will stick to the general BytesWritable.

That is all very well, but how to generate a sequence file from the images files? Since there are lots of them it may prove not an easy 
task at all. There are some ways that I had thought of:

1. If possible the best way is to generate the sequence file as soon as the images are acquired. That way the system is always ready
for image processing without the need to do some preprocessing to generate the sequence file. Since sequence files can be appended to 
this can be applied also if the images are not being retrieved all at once.  We can do this by using class 
org.apache.hadoop.io.SequenceFile.Writer

2. Storing all the images as a tar file and using the tool written by Stuart Sierra to convert it to a sequence file.

3. Using a map reduce job to collect all the image files and store them as a sequence file. The map input will be the HDFS path of 
those images and the map output will the the sequence file (no reduce task in this case). That way using Hadoop scalable advantages 
in case you really have a huge amount of files. This is inspired by a simple tool written by Ryan Balfanz.

I’ll demonstrate creating the sequence file according to the third option since I believe it is the most general way to do it.

Before starting digging into code an immediate question floats in the mind. If I’m bothering to load all the images bytes into the memory in order to write them to a sequence file, won’t I would suffer from all the problems related to a huge amount of small files that I had mentioned above? And also if the images are in the memory won’t it be best to do the image processing – finding image duplicates – at this stage rather than creating a sequence file and only then try to find the duplicates?

The answer has two different aspects –

    1. In case the image processing include more than just image duplicate finder and there are more than a single map reduce job that need 
    to read all the images, then you would like it to be as a sequence file in order to perform better
    
    2. The pre process of converting the images into a sequence file can be done for each image independently if we are not having all the 
    images yet. The duplicate finder however, is a process that needs all the images in order to be processed. Decoupling the job of 
    creating the sequence file from the job of finding the duplicates can be used in some cases to design the system in a way that the 
    sequence file creation will be invoked on the images even before all the images are present.

Pre Processing job – generating the sequence file
--------------------------------------------

The target is a sequence file which has the filename as the key and the BytesWritable as the value. The input is a file contains all 
the image file as HDFS filenames. For example:

hdfs://localhost:8022/user/arun/smallArchiveImages/WonderWoman.jpg

So our map / reduce job will include only a map that will read one file at a time and write it to a sequence file by using 
SequenceFileOutputFormat. It uses a FileSystem object in order to open the HDFS file and FSDataInputStream in order to read 
from it. The byte array is being written to the context as a bytewritable. Since the output format of the job is 
SequenceFileOutputFormat class, the output of the map is being written to a sequence file.

This is demonstrated in the BinaryFilesToHadoopSequenceFile.java which implements this pre process job.

Images Duplicates Finder job
--------------------------------

Now we have a sequence file with all the files binary data and it is time for the actual job that will filter the duplicates. 
We will use the MD5 algorithm to generate a unique key for each image and compare this key in order to find the duplicates. 
Our map / reduce job will include:

        1. A mapper that will read the binary image data of all the image files and will create MD5 presentation for each file. 
        It will pass this data to the reducer where the key will be the MD5 string while the value will be the filename. Thus, all 
        the identical images will be grouped together by the hadoop framework.

        public void map(Text key, BytesWritable value, Context context) throws IOException,InterruptedException {
        	 //get the md5 for this specific file String md5Str; 
        	 try { md5Str = calculateMd5(value.getBytes()); } 
        	 catch (NoSuchAlgorithmException e) {
        	 	 e.printStackTrace(); context.setStatus("Internal error - can't find the algorithm for calculating the md5"); 
        	 	 return;
        	 }
        	Text md5Text = new Text(md5Str);  
        	//put the file in the map where the md5 is the key, so duplicates  
        	//will be grouped together for the reduce function
        	context.write(md5Text, key); 
        } 
        static String calculateMd5(byte[] imageData) throws NoSuchAlgorithmException {
         //get the md5 for this specific data 
         MessageDigest md = MessageDigest.getInstance("MD5"); 
         md.update(imageData); byte[] hash = md.digest(); 
         // Below code of converting Byte Array to hex 
         String hexString = new String(); 
         for (int i=0; i < hash.length; i++) { 
         	hexString += Integer.toString( ( hash[i] & 0xff ) + 0x100, 16).substring( 1 ); 
         	} 
         return hexString; 
        } 

        2. A very simple reducer that will only take the first filename for each MD5 hash. That way there will be only a single 
        filename for each identical image and all the duplicates are filtered. The output is a map file where the key is the 
        filename and the value is the MD5 hash

		public void reduce(Text key, Iterable<Text> values,
		  Context context) throws IOException, InterruptedException {
		   //Key here is the md5 hash while the values are all the image files that
		 // are associated with it. for each md5 value we need to take only
		 // one file (the first)
		  Text imageFilePath = null;
		  for (Text filePath : values) {
		    imageFilePath = filePath;
		    break;//only the first one
		  }
		  // In the result file the key will be again the image file path. 
		  context.write(imageFilePath, key);
		}

All this stuff is demonstrated in class ImageDuplicatesRemover.java which implements the duplicates remover job.





