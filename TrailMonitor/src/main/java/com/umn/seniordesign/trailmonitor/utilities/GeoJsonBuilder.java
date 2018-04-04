package com.umn.seniordesign.trailmonitor.utilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.umn.seniordesign.trailmonitor.entities.TrailPointRecord;
import com.umn.seniordesign.trailmonitor.entities.geojson.Feature;
import com.umn.seniordesign.trailmonitor.entities.geojson.GeoJson;
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
	
	//TODO: Document function
	public static Map<String, GeoJson> build(Map<Integer, List<TrailPointRecord>> tileRecords, int zoomDepth) throws Exception { 
		//lists of features to be plotted
		List<Feature> pointFeatures = new LinkedList<Feature>();
		List<Feature> lineFeatures = new LinkedList<Feature>();
		
		Iterator<Map.Entry<Integer,List<TrailPointRecord>>> tileIterator = tileRecords.entrySet().iterator();
		int divisions;
		int startDepth;
		if(zoomDepth == 3) {
			divisions = 100;
			startDepth = 1;
		}
		else if(zoomDepth == 2) {
			divisions = 10;
			startDepth = 1;
		} else {
			divisions = 100;
			startDepth = 0;
		}
		while(tileIterator.hasNext()) {
			Map.Entry<Integer,List<TrailPointRecord>> tile = tileIterator.next();
			//TODO: get top-bottom-left-right
			//TODO: process tile
		}
		
		//type "FeatureCollection" which contains a list of features to be plotted
		GeoJson pointData = new GeoJson(GeoJson.Types.FeatureCollection);
		pointData.setFeatures(pointFeatures);
		GeoJson lineData = new GeoJson(GeoJson.Types.FeatureCollection);
		lineData.setFeatures(lineFeatures);
		Map<String, GeoJson> processedData = new HashMap<String, GeoJson>(2, 1.0F);
		processedData.put("pointData", pointData);
		processedData.put("lineData", lineData);
		return processedData;
	}
	
	//TODO: Document function
	public static void processArea(double top, double bot, double left, double right, int divisions, int depth, 
			List<TrailPointRecord> points, List<Feature> outPoints, List<Feature> outLines) {
		
		double xScale = (right - left) / divisions; //longitude-distance / division
		double yScale = (top - bot) / divisions; //latitude-distance / division
		
		Bucket[][] grid = new Bucket[divisions][divisions]; //rectangular grid that all points will be mapped into
		List<Bucket> populatedBuckets = new LinkedList<Bucket>(); //list of non-empty buckets that have points
		Iterator<TrailPointRecord> iterRawPoints = points.iterator();
		
		int y;
		int x;
		while(iterRawPoints.hasNext()) { //place each point in the appropriate bucket based on it's lat and lng
			TrailPointRecord point = iterRawPoints.next();
			y = (int)Math.floor((point.getLatitude() - bot) / yScale);
			x = (int)Math.floor((point.getLongitude() - left) / xScale);
			
			if(grid[y][x] == null) { //if bucket is empty/null create new bucket at this position
				grid[y][x] = new Bucket(y, x);
			}
			grid[y][x].add(point, depth <= 0); //add point to bucket
			populatedBuckets.add(grid[y][x]);
		}
		
		if(depth <= 0) { //base/deepest depth - compute lines
			//TODO: compute lines
		}
		else {
			Iterator<Bucket> buckIter = populatedBuckets.iterator();
			Bucket bucket;
			double newBot;
			double newLeft;
			while(buckIter.hasNext()) {
				bucket = buckIter.next();
				newBot = bot + bucket.getY() * yScale;
				newLeft = left + bucket.getX() * xScale;
				processArea(newBot + yScale, newBot, newLeft, newLeft + xScale, 100, depth - 1,
						bucket.getPoints(), outPoints, outLines);
			}
		}
		
	}
	
	static private class Bucket {
		private List<TrailPointRecord> points;
		private int y;
		private int x;
		private double totalValues;
		private Set<String> deviceIds;
		private long timeStamp;
		
		public Bucket(int latIndex, int lngIndex) {
			this.points = new LinkedList<TrailPointRecord>();
			this.y = latIndex;
			this.x = lngIndex;
			this.totalValues = 0;
			this.timeStamp = 0;
		}
		
		public void add(TrailPointRecord point, boolean process) {
			points.add(point);
			if(process) { //combine information from all points in bucket
				this.totalValues += point.getValue();
				if(deviceIds == null) {
					this.deviceIds = new HashSet<String>(50, 1.0F);
				}
				this.deviceIds.add(point.getDeviceId());
				long time = point.getTimeStamp().getTimeInMillis();
				if(this.timeStamp < time) {
					this.timeStamp = time;
				}
			}
		}
		
		public int size() {
			return this.points.size();
		}
		
		public int getValue() {
			return (int)Math.round(this.totalValues / this.size());
		}
		
		public List<TrailPointRecord> getPoints() {
			return this.points;
		}
		
		public int getX() {
			return this.x;
		}
		
		public int getY() {
			return this.y;
		}
		
		public Set<String> getDeviceIds() {
			return this.deviceIds;
		}
		
		public long getTimeStamp() {
			return this.timeStamp;
		}
		
	}

}
