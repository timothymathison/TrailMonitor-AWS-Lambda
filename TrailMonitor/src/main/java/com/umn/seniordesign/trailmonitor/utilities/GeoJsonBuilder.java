package com.umn.seniordesign.trailmonitor.utilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.umn.seniordesign.trailmonitor.entities.TrailPointRecord;
import com.umn.seniordesign.trailmonitor.entities.geojson.Feature;
import com.umn.seniordesign.trailmonitor.entities.geojson.GeoJson;
import com.umn.seniordesign.trailmonitor.entities.geojson.GeoJson.Types;
import com.umn.seniordesign.trailmonitor.entities.geojson.Geometry;
import com.umn.seniordesign.trailmonitor.entities.geojson.Properties;

public class GeoJsonBuilder {

	/**
	 * <h1>Builds a GeoJson object containing features which can be interpreted and displayed on a map</h2>
	 * @param tileRecords - A map of lists each containing objects of class type TrailPointRecord for a particular map tile
	 * @return Object of class type GeoJson
	 * @throws Exception Thrown when the GeoJson is improperly built
	 */
	public static GeoJson build(Map<Integer, List<TrailPointRecord>> tileRecords) throws Exception {
		GeoJson geoJson = new GeoJson(GeoJson.Types.FeatureCollection);  
		//type "FeatureCollection" which contains a list of features to be plotted
		List<Feature> features = new LinkedList<Feature>();
		TrailPointRecord record;
		List<TrailPointRecord> tile;
		Geometry<Double> geometry;
		Properties properties;
		
		Iterator<Map.Entry<Integer,List<TrailPointRecord>>> tileIterator = tileRecords.entrySet().iterator();
		Iterator<TrailPointRecord> recordIterator;
		while(tileIterator.hasNext()) { //iterate through tiles, which are each a list of trail records
			tile = tileIterator.next().getValue();
			recordIterator = tile.iterator();
			while(recordIterator.hasNext()) { //iterate through trail records and create features
				record = recordIterator.next();
				geometry = new Geometry<Double>(Arrays.asList(record.getLongitude(), record.getLatitude()));
				properties = new Properties(record.getValue().intValue(), record.getDeviceId(), record.getTimeStamp().getTimeInMillis());
				features.add(new Feature(geometry, properties));
			}	
		}
		geoJson.setFeatures(features);
		
		return geoJson;
	}
	
	public static Map<String, GeoJson> build(Map<Integer, List<TrailPointRecord>> tileRecords, int zoomDepth) throws Exception { 
		//type "FeatureCollection" which contains a list of features to be plotted
		GeoJson pointData = new GeoJson(GeoJson.Types.FeatureCollection);
		GeoJson lineData = new GeoJson(GeoJson.Types.FeatureCollection);
		
		
		Map<String, GeoJson> processedData = new HashMap<String, GeoJson>(2, 1.0F);
		processedData.put("pointData", pointData);
		processedData.put("lineData", lineData);
		return processedData;
	}
	
	public static void processArea(double top, double bot, double left, double right, int divisions, int depth, 
			List<TrailPointRecord> points, List<Feature> outPoints, List<Feature> outLines) {
		
		Bucket[][] grid = new Bucket[divisions][divisions]; //rectangular grid that all points will be mapped into
		
	}
	
	private class Bucket {
		private List<TrailPointRecord> points;
		private int latIndex;
		private int lngIndex;
		
		public Bucket(int latIndex, int lngIndex) {
			this.points = new LinkedList<TrailPointRecord>();
			this.latIndex = latIndex;
			this.lngIndex = lngIndex;
		}
		
		public void add(TrailPointRecord point) {
			points.add(point);
		}
		
	}

}
