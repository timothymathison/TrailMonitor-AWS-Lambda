package com.umn.seniordesign.trailmonitor.utilities;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.umn.seniordesign.trailmonitor.entities.GPSTuple;
import com.umn.seniordesign.trailmonitor.entities.TrailPoint;
import com.umn.seniordesign.trailmonitor.entities.TrailPointRecord;

public class DataConverter {

	/**
	 * <h1>Converts TrailPoint class to TrailPointRecord (database class)</h1>
	 * @param data - TrailPoint class object
	 * @param deviceId - deviceId associated with data
	 * @return TrailPointRecord class object
	 * @throws Exception Invalid data
	 */
	public static TrailPointRecord makeRecord(TrailPoint data, String deviceId) throws Exception {
		if(!data.valid()) { //check validity and completeness of data
			throw new Exception("Missing or invalid data");
		}
		
		TrailPointRecord record = new TrailPointRecord();
		record.setAllCoordinates(data.getLatitude(), data.getLongitude());
		record.setTimeStampFromMilli(data.getTimeStamp());
		record.setDeviceId(deviceId);
		record.setValue(data.getValue());
		return record;
	}
	
	/**
	 * <h1>Converts list of TrailPoint class objects to TrailPointRecord (database class) objects</h1>
	 * Calls {@link #makeRecord(TrailPoint, long)} for each list element
	 * @param data - List of TrailPoint class objects
	 * @param deviceId - deviceId associated with data
	 * @return List of TrailPointRecord objects
	 * @throws Exception Thrown if data is in-valid
	 */
	public static List<TrailPointRecord> makeRecords(List<TrailPoint> data, String deviceId) throws Exception {
		List<TrailPointRecord> records = new LinkedList<TrailPointRecord>();
		Iterator<TrailPoint> iterator = data.iterator();
		
		while(iterator.hasNext()) {
			records.add(makeRecord(iterator.next(), deviceId));
		}
		
		return records;
	}
	
	//TODO: figure out how to make this function work, or get rid of it
	public static void setDatabaseTableToBeta() throws Exception {
		final String tableName = "TrailData_Beta";
		
		final DynamoDBTable oldAnnotation = (DynamoDBTable)TrailPointRecord.class.getAnnotations()[0];
//	    System.out.println("oldAnnotation = " + oldAnnotation.someProperty());
	    Annotation newAnnotation = new DynamoDBTable() {

	        @Override
	        public String tableName() {
	            return tableName;
	        }

	        @Override
	        public Class<? extends Annotation> annotationType() {
	            return oldAnnotation.annotationType();
	        }
	    };
	    Field field = Class.class.getDeclaredField("annotations");
	    field.setAccessible(true);
	    Map<Class<? extends Annotation>, Annotation> annotations = (Map<Class<? extends Annotation>, Annotation>) field.get(TrailPointRecord.class);
	    annotations.put(DynamoDBTable.class, newAnnotation);

//	    DynamoDBTable modifiedAnnotation = (DynamoDBTable)TrailPointRecord.class.getAnnotations()[0];
//	    System.out.println("modifiedAnnotation = " + modifiedAnnotation.tableName());
		
	}
	
	/**
	 * <h1>Generates a unique linear value (tile identifier/coordinate) for each integer latitude/longitude combination</h1>
	 * @param longitude - number of type Double representing a GPS longitude value
	 * @param latitude - number of type Double representing a GPS latitude value
	 * @return Value of type int identifying an Integer latitude/longitude tile
	 */
	public static int reduceCoordinateDimension(Double longitude, Double latitude) {
		return reduceCoordinateDimension(((int)Math.floor(longitude)), ((int)Math.floor(latitude)));
	}
	
	/**
	 * <h1>Generates a unique linear value (tile identifier/coordinate) for each integer latitude/longitude combination</h1>
	 * @param longitude - number of type int representing a GPS longitude value
	 * @param latitude - number of type int representing a GPS latitude value
	 * @return Value of type int identifying an Integer latitude/longitude tile
	 */
	public static int reduceCoordinateDimension(int longitude, int latitude) {
		return longitude * 200 + latitude;
	}
	
	//TODO: Document function
	public static GPSTuple expandCoordinateDimension(int coord) {
		Double temp = ((double)coord + 90) / 200;
		Double lng = Math.floor(temp);
		return new GPSTuple(lng, Math.round((temp - lng) * 200) - 90);
	}
	
	/**
	 * <h1>Parses input params, describing a rectangular area relative to GPS, into a list of linear single dimensional tile coordinates</h1>
	 * @param params - An object of type Map containing values for "lim-top", "lim-right", "lim-left", and "lim-bot"
	 * @return A list of Integer global tile coordinates
	 */
	public static List<Integer> parseRequestCoords(Map<String, String> params) {
		try {
			int top = (int)Math.floor(Double.parseDouble(params.get("lim-top")));
			int right = (int)Math.floor(Double.parseDouble(params.get("lim-right")));
			int left = (int)Math.floor(Double.parseDouble(params.get("lim-left")));
			int bot = (int)Math.floor(Double.parseDouble(params.get("lim-bot")));
			
			if(top < bot || right < left) { //limits are invalid
				return new ArrayList<Integer>();
			}
			int height = top - bot + 1;
			int width = right - left + 1;
			
			List<Integer> tileCoords = new ArrayList<Integer>(height * width);
			for(int lat = top; lat >= bot; lat--) {
				for(int lon = left; lon <= right; lon++) {
					tileCoords.add(reduceCoordinateDimension(lon, lat)); //add single dimension tile coordinates to list
				}
			}
			
			return tileCoords;
		}
		catch(NullPointerException | NumberFormatException e) { //failed to parse limit parameters
			return new ArrayList<Integer>(); //return empty list
		}
	}
	
	/**
	 * @param datetime - Calendar object representing a point in time
	 * @return String formatted time stamp relative to UTC time zone
	 */
	public static String timeStamp(Calendar datetime) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timestr = dateFormatter.format(datetime.getTime());
        return timestr;
	}
}
