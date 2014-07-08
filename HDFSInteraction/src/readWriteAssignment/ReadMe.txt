1. Code for accessing HDFS file system

         public static void main(String[] args) throws IOException {
         FileSystem hdfs =FileSystem.get(new Configuration());
         Path homeDir=hdfs.getHomeDirectory();
         //Print the home directory
         System.out.println("Home folder -" +homeDir); 
		}

2. code for creating and deleting directory

        Path workingDir=hdfs.getWorkingDirectory();
		Path newFolderPath= new Path("/MyDataFolder");
		newFolderPath=Path.mergePaths(workingDir, newFolderPath);
		if(hdfs.exists(newFolderPath)){
      		hdfs.delete(newFolderPath, true); //Delete existing Directory
		}
		hdfs.mkdirs(newFolderPath);     //Create new Directory

3. Code for copying File from local file system to HDFS

        Path localFilePath = new Path("c://localdata/datafile1.txt");
		Path hdfsFilePath=new Path(newFolderPath+"/dataFile1.txt");
		hdfs.copyFromLocalFile(localFilePath, hdfsFilePath);

4. Copying File from HDFS to local file system

         localFilePath=new Path("c://hdfsdata/datafile1.txt");
		 hdfs.copyToLocalFile(hdfsFilePath, localFilePath);

5. Creating a file in HDFS

         Path newFilePath=new Path(newFolderPath+"/newFile.txt");
		hdfs.createNewFile(newFilePath);

6. Writing data to a HDFS file

         StringBuilder sb=new StringBuilder();
		 for(int i=1;i<=5;i++){
            sb.append("Data");
            sb.append(i);
            sb.append("\n");
          }
		  byte[] byt=sb.toString().getBytes();
		  FSDataOutputStream fsOutStream = hdfs.create(newFilePath);
		  fsOutStream.write(byt);
		  fsOutStream.close();

7. Reading data From HDFS File

        BufferedReader bfr=new BufferedReader(new InputStreamReader(hdfs.open(newFilePath)));     
		String str = null;
		while ((str = bfr.readLine())!= null){
            System.out.println(str);
      	}
      	
++++++++++++++++++++++++++++++++++++++++++
Any Java program can talk to HDFS, provided that program is set up right

    The classpath contains the Hadoop JAR files and its client-side dependencies. (we are being vague here as those dependencies varies from version to version).
    The hadoop configuration files on the classpath

    Log4J on the classpath along with a log4.properties resource, or commons-logging preconfigured to use a different logging framework. 

Step By Step

Create a FileSystem instance by passing a new Configuration object. Please note that the following example code assumes that the Configuration object will automatically load the hadoop-default.xml and hadoop-site.xml configuration files. You may need to explicitly add these resource paths if you are not running inside of the Hadoop runtime environment.

Configuration conf = new Configuration();
FileSystem fs = FileSystem.get(conf);

Given an input/output file name as string, we construct inFile/outFile Path objects. Most of the FileSystem APIs accepts Path objects.

Path inFile = new Path(argv[0]);
Path outFile = new Path(argv[1]);

Validate the input/output paths before reading/writing.

if (!fs.exists(inFile))
  printAndExit("Input file not found");
if (!fs.isFile(inFile))
  printAndExit("Input should be a file");
if (fs.exists(outFile))
  printAndExit("Output already exists");

Open inFile for reading.

FSDataInputStream in = fs.open(inFile);

Open outFile for writing.

FSDataOutputStream out = fs.create(outFile);

Read from input stream and write to output stream until EOF.

while ((bytesRead = in.read(buffer)) > 0) {
  out.write(buffer, 0, bytesRead);
}

Close the streams when done.

in.close();
out.close();