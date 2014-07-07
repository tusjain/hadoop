package wordCount;

import org.apache.hadoop.io.*;
import java.io.*;

//public class Duo implements WritableComparable<Duo>{
public class Duo implements Writable{
	public String property;
	public String object;
	public Duo(){
		set(new String(),new String());
	}
	public Duo(String p, String o){
		set(p,o);
	}
	public void set(String p, String o){
		property=p;
		object=o;
	}
	public String getProperty(){
		return property;
	}
	public String getObject(){
		return object;
	}
	public void write(DataOutput out) throws IOException {
		out.writeChars(property);
		out.writeChars(object);
	}
	public void readFields(DataInput in) throws IOException {
		property = in.readLine();
		object = in.readLine();
	}
	public int hashCode(){
		return property.hashCode()* 163 +object.hashCode();
	}
	public boolean equals(Object other){
		if (other instanceof Duo){
			Duo od = (Duo) other;
			return property.equals(od.property) && object.equals(od);
		}
		return false;
	}
	public String toString(){
		return property + "\t" + object;
	}
	public int compareTo(Duo other){
		if (property.compareTo(other.property)==0)
			return object.compareTo(other.object);
		else
			return property.compareTo(other.property);
	}
}