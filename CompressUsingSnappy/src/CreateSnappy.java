import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
 
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.SnappyCodec;
import org.apache.hadoop.util.ReflectionUtils;
 
/*
*This program compresses the given file in snappy format
*
*/
 
public class CreateSnappy {
public static void main(String[] args) {
if (args.length < 2) {
System.out.println("Enter <input> <output>");
System.exit(0);
}
 
try {
CompressionCodec codec = (CompressionCodec) ReflectionUtils
.newInstance(SnappyCodec.class, new Configuration());
OutputStream outStream = codec
.createOutputStream(new BufferedOutputStream(
new FileOutputStream(args[1])));
InputStream inStream = new BufferedInputStream(new FileInputStream(
args[0]));
int readCount = 0;
byte[] buffer = new byte[64 * 1024];
while ((readCount = inStream.read(buffer)) > 0) {
outStream.write(buffer, 0, readCount);
}
inStream.close();
outStream.close();
System.out.println("File Compressed");
 
} catch (Exception e) {
e.printStackTrace();
}
}
} 