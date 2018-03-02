package com.umn.seniordesign.trailmonitor.utilities;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.umn.seniordesign.trailmonitor.entities.GeoJson;
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
	public static TrailPointRecord makeRecord(TrailPoint data, long deviceId) throws Exception {
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
	public static List<TrailPointRecord> makeRecords(List<TrailPoint> data, long deviceId) throws Exception {
		List<TrailPointRecord> records = new LinkedList<TrailPointRecord>();
		Iterator<TrailPoint> iterator = data.iterator();
		
		while(iterator.hasNext()) {
			records.add(makeRecord(iterator.next(), deviceId));
		}
		
		return records;
	}
	
	/**
	 * <h1>Builds a GeoJson object containing features which can be interpreted and displayed on a map</h2>
	 * @param records - List containing objects of class type TrailPointRecord
	 * @return Object of class type GeoJson
	 * @throws Exception Thrown when the GeoJson is improperly built
	 */
	public static GeoJson buildGeoJson(List<TrailPointRecord> records, LambdaLogger l) throws Exception {
		l.log("# of records: " + records.size());
		GeoJson geoJson = new GeoJson(GeoJson.Types.FeatureCollection);  
		//type "FeatureCollection" which contains a list of features to be plotted
		List<GeoJson.Feature> features = new LinkedList<GeoJson.Feature>();
		TrailPointRecord record;
		GeoJson.Geometry<Double> geometry;
		GeoJson.Properties properties;
		
		Iterator<TrailPointRecord> iterator = records.iterator();
		while(iterator.hasNext()) { //iterate through trail records and create features
			record = iterator.next();
			geometry = new GeoJson.Geometry<Double>(Arrays.asList(record.getLongitude(), record.getLatitude()));
			properties = new GeoJson.Properties(record.getValue().intValue(), record.getDeviceId().intValue(), record.getTimeStamp().getTimeInMillis());
			features.add(new GeoJson.Feature(geometry, properties));
		}
		l.log("# of features: " + records.size());
		geoJson.setFeatures(features);
		
		return new GeoJson(GeoJson.Types.FeatureCollection);
	}
	
	/**
	 * <h1>Generates a unique linear value (tile identifier) for each integer latitude/longitude combination</h1>
	 * @param longitude - number of type Double representing a GPS longitude value
	 * @param latitude - number of type Double representing a GPS latitude value
	 * @return Value of type int identifying an latitude/longitude tile
	 */
	public static int reduceCoordinateDimension(Double longitude, Double latitude) {
		return ((int)Math.floor(longitude)) * 200 + ((int)Math.floor(latitude));
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
