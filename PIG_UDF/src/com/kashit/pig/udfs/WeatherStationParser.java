package com.kashit.pig.udfs;

import java.io.IOException; 
import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;

public class WeatherStationParser extends EvalFunc {
    TupleFactory tupleFactory = TupleFactory.getInstance();

    /*
     * The exec function takes a tuple as an argument. Remeber that
     * a tuple is simply a collection fields that are primitive types.
     */
    @Override
    public Tuple exec(Tuple tuple) throws IOException {
        //Expect the unexpected and then handle it gracefully
        if (tuple == null || tuple.size() != 1)
            return null;
        try{
            //Get the data row
            String row = (String)tuple.get(0);

            /*
             * Kludgy...but effective filter header rows
             * by length. Should do regex pattern matching
             */
            if (row.length() > 95){
                //Use the tuple factory to create a new tuple                                
                Tuple t = tupleFactory.newTuple();

                t.append(GetIntFromString(row, 0, 6)); //USAF_ID
                t.append(GetIntFromString(row, 7, 13)); //WBAN_ID
                t.append(ParseString(row, 13, 43)); //STATION_ID       
                t.append(GetLatitude(GetFloatFromString(row, 58, 64))); //LAT
                t.append(GetLongitude(GetFloatFromString(row, 65, 73))); //LOT
                //Return the tuple
                return t;
            } else {
                /*
                 * For header rows, we will simply return NULL
                 * we will filter the NULLs out in the query
                 */                
                return null;
            }
        } catch (Exception ex){
            /*
             * Uh-oh something went wrong....
             * Return NULL...consider logging this error
             * if its important
             */
            return null;
        }
    }

    /*
     * Helper Methods
     */
    private Float GetLatitude(float val){
        //Filter out NULL Latitudes
        if (val == 0f || Math.abs(val) == 99999f)
            return null;

        //Latitudes are in decimal degree format
        return val/1000f;
    }

    private Float GetLongitude(float val){
        //Filter out NULL Longitudes
        if (val == 0f || Math.abs(val) == 999999f)
            return null;

        //Longitudes are in decimal degree format
        return val/1000f;
    }

    private String ParseString(String s, int start, int stop) {
        //Parse the string 
        return s.substring(start, stop).trim();
    }

    private int GetIntFromString(String s, int start, int stop){
        /*
         * Parse the string and remove the '+' plus sign 
         * before we convert it to an integer
         */        
        String substring = s
                .substring(start, stop)
                .replace("+", "")
                .trim();
        return Integer.parseInt(substring);
    }

    private float GetFloatFromString(String s, int start, int stop){
        /*
         * Parse the string and remove the '+' plus sign 
         * before we convert it to an float
         */
        String substring = s
                .substring(start, stop)
                .replace("+", "")
                .trim();
        return Float.parseFloat(substring);
    }
}