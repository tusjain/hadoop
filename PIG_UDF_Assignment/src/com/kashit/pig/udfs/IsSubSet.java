package com.kashit.pig.udfs;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
 
import org.apache.pig.EvalFunc;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.DataType;
import org.apache.pig.data.Tuple;
import org.apache.pig.impl.logicalLayer.schema.Schema;
import org.apache.pig.impl.logicalLayer.schema.Schema.FieldSchema;
 
/**
 * Find the whether given SetB is subset of SetA.
 * <p>
 *  input:
 * <br>setA : {(10),(4),(21),(9),(50)}</br>
 * <br>setB : {(9),(4),(50)}</br>
 * <br></br>
 *  output:
 * <br>true</br>
 * </p>
 */
public class IsSubSet extends EvalFunc {
 
    @Override
    public Schema outputSchema(Schema input) {
        if(input.size()!=2){
            throw new IllegalArgumentException("input should contains two elements!");
        }
 
        List fields = input.getFields();
        for(FieldSchema f : fields){
            if(f.type != DataType.BAG){
                throw new IllegalArgumentException("input fields should be bag!"); 
            }
        }
        return new Schema(new FieldSchema("isSubset",DataType.BOOLEAN));
    }
 
    private Set populateSet(DataBag dataBag){
        HashSet set = new HashSet();
 
        Iterator iter = dataBag.iterator();
        while(iter.hasNext()){
            set.add(iter.next());
        }
        return set;
    }
 
    @Override
    public Boolean exec(Tuple input) throws IOException {
 
        Set setA = populateSet((DataBag) input.get(0));
        Set setB = populateSet((DataBag) input.get(1));
        return setA.containsAll(setB) ? Boolean.TRUE : Boolean.FALSE;
    }
}