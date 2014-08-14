1. The WeatherStationParser class needs to extend or inherit from the EvalFunc<T> abstract class.
	This class has a single abstract method called exec(Tuple) which you must provide an implementation for.
	The implementation of this method is what determines the behavior that your UDF will have.
	In this example, we handle the parsing of the row into its distinct fields.

2.  With your class complete, you are ready to build it and package it into a jar file.

3.  Let's use our new UDF. First, you will need to register the jar file and then define the function. 
	When you define the function you will need to use the fully-qualified path including the namespace. 
	You will also need to project a schema onto the data using an alias. We can rewrite the script as follows:

	register '\myUDFs\com.kashit.pig.udfs.jar'
	define StationParser com.kashit.pig.udfs.WeatherStationParser();
	
	ish = load '/user/Administrator/WeatherData/Stations.txt' as (row:chararray);
	stations = foreach ish generate flatten(StationParser(row)) as 
		(USAF_ID: int, WBAN_ID: int, STATION_ID: chararray,
		LATITUDE: float, LONGITUDE: float);
	stationsFiltered = filter stations by USAF_ID is not NULL;
	
	
	
4.  In the prior UDF, we implemented only the exec(Tuple) function to parse out the station data. 
	The result of this requires that we define data schema within the script. While this provides an 
	added measure of flexibility it also requires that we do more in the Pig Script (and if your going
	through the trouble to build a UDF why not include the schema.

 