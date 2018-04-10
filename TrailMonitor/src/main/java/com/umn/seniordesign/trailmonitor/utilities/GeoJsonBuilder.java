package com.umn.seniordesign.trailmonitor.utilities;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.umn.seniordesign.trailmonitor.entities.GPSTuple;
import com.umn.seniordesign.trailmonitor.entities.GeoTrailInfo;
import com.umn.seniordesign.trailmonitor.entities.TrailPointRecord;
import com.umn.seniordesign.trailmonitor.entities.geojson.Feature;
import com.umn.seniordesign.trailmonitor.entities.geojson.GeoJsonTile;
import com.umn.seniordesign.trailmonitor.entities.geojson.Geometry;
import com.umn.seniordesign.trailmonitor.entities.geojson.Properties;

public class GeoJsonBuilder {

	/**
	 * <h1>Builds a GeoTrailInfo object containing GeoJsonTiles with features that can be displayed on a map</h2>
	 * @param tileRecords - A map of lists each containing objects of class type TrailPointRecord for a particular map tile
	 * @return Object of class type {@link #GeoTrailInfo}
	 * @throws Exception Thrown if a GeoJsonTile is improperly built
	 */
	public static GeoTrailInfo build(Map<Integer, List<TrailPointRecord>> tileRecords) throws Exception {
		List<GeoJsonTile> geoJsonTiles = new LinkedList<GeoJsonTile>();
		
		Map.Entry<Integer,List<TrailPointRecord>> tileRecord;
		TrailPointRecord record;
		List<TrailPointRecord> records;
		GeoJsonTile tile;
		List<Feature> features;
		long featureCount = 0;
		Geometry<Double> geometry;
		Properties properties;
		
		Iterator<Map.Entry<Integer,List<TrailPointRecord>>> tileIterator = tileRecords.entrySet().iterator();
		Iterator<TrailPointRecord> recordIterator;
		while(tileIterator.hasNext()) { //iterate through tiles, which are each a list of trail records
			tileRecord = tileIterator.next();
			records = tileRecord.getValue();
			recordIterator = records.iterator();
			features = new LinkedList<Feature>(); //create new list of features for current tile
			while(recordIterator.hasNext()) { //iterate through trail records and create features
				record = recordIterator.next();
				geometry = new Geometry<Double>(Arrays.asList(record.getLongitude(), record.getLatitude()));
				properties = new Properties(record.getValue().intValue(), 1, record.getDeviceId(), record.getTimeStamp().getTimeInMillis());
				features.add(new Feature(geometry, properties));
				featureCount++;
			}
			//create GeoJsonTile and assign features to it
			tile = new GeoJsonTile(GeoJsonTile.Types.FeatureCollection, 
					DataConverter.expandCoordinateDimension(tileRecord.getKey()));
			tile.setPointData(features);
			geoJsonTiles.add(tile); //add to list of processed tiles
		}
		GeoTrailInfo geoTrailInfo = new GeoTrailInfo(geoJsonTiles, featureCount);
		
		return geoTrailInfo;
	}
	
	/**
	 * <h1>Processes TrailPointRecords and builds a GeoTrailInfo object containing GeoJsonTiles 
	 * with features that can be displayed on a map.</h2>
	 * Calls {@link #GeoJsonBuilder}.processArea() to process raw points and produce a depiction of trail conditions.
	 * Output detail depends on requested zoomDepth. At highest zoomDepth, function produces both point and line features.
	 * @param tileRecords - A map of lists each containing objects of class type {@link #TrailPointRecord} for a particular map tile
	 * @param zoomDepth - Used to determine processing detail (2 -> highest detail, 0 -> lowest detail)
	 * @return Object of class type {@link #GeoTrailInfo}
	 * @throws Exception Thrown if called with invalid zoomDepth or a GeoJsonTile is improperly built
	 */
	public static GeoTrailInfo build(Map<Integer, List<TrailPointRecord>> tileRecords, int zoomDepth) throws Exception { 
		
		//calculations for determining how to process data
		int divisions;
		int startDepth;
		boolean computeLines = false;
		if(zoomDepth == 2) { // tile divided into 10,000 x 10,000 grid, each bucket is roughly 36 x 36 feet
			divisions = 100;
			startDepth = 1;
			computeLines = true; //zoom is great enough, draw lines between populated buckets
		}
		else if(zoomDepth == 1) { // tile divided into 1,000 x 1,000 grid
			divisions = 10;
			startDepth = 1;
		}
		else if(zoomDepth == 0) { // tile divided into 100 x 100 grid
			divisions = 100;
			startDepth = 0;
		}
		else {
			throw new Exception("Invalid zoomDepth sent to GeoTrailInfo build function, must be in range 1-3");
		}
		String zoomRange = GeoTrailInfo.availableZoomRanges.get(zoomDepth);
		
		//declarations for iterating
		Map.Entry<Integer,List<TrailPointRecord>> tileRecord;
		Iterator<Map.Entry<Integer,List<TrailPointRecord>>> tileIterator = tileRecords.entrySet().iterator();
		
		List<GeoJsonTile> geoJsonTiles = new LinkedList<GeoJsonTile>(); //list of GeoJsonTiles that will be returned
		
		//temp variable declarations
		GeoJsonTile tile;
		GPSTuple<Double, Double> tileCorner;
		//lists of features to be plotted
		List<Feature> pointFeatures;
		List<Feature> lineFeatures;
		long featureCount = 0;
		while(tileIterator.hasNext()) {
			tileRecord = tileIterator.next();
			if(tileRecord.getValue().size() > 0) {
				//get top-bottom-left-right
				tileCorner = DataConverter.expandCoordinateDimension(tileRecord.getValue().get(0).getCoordinate());
				double top = tileCorner.lat + 1;
				double bot = tileCorner.lat;
				double left = tileCorner.lng;
				double right = left + 1;
				
				//create new tile, type FeatureCollection
				tile = new GeoJsonTile(GeoJsonTile.Types.FeatureCollection, tileCorner, zoomRange);
				pointFeatures = new LinkedList<Feature>();
				lineFeatures = new LinkedList<Feature>();
				//process tile
				processArea(top, bot, left, right, divisions, startDepth, computeLines, tileRecord.getValue(), pointFeatures, lineFeatures);
				tile.setPointData(pointFeatures);
				tile.setLineData(lineFeatures);
				geoJsonTiles.add(tile);
				featureCount += pointFeatures.size() + lineFeatures.size();
			}
		}
		
		return new GeoTrailInfo(zoomRange, geoJsonTiles, featureCount);
	}
	
	/**
	 * <h2>Maps all trail points into buckets contained within a grid system, and combines points which are mapped to the same bucket.</h2>
	 * Depending on requested depth, function will execute recursively to increase the detail (higher depth = more recursive calls)
	 * At every recursive call, depth decreases by 1 until it reaches 0
	 * @param top
	 * @param bot
	 * @param left
	 * @param right
	 * @param divisions
	 * @param depth
	 * @param points
	 * @param outPoints
	 * @param outLines
	 */
	public static void processArea(double top, double bot, double left, double right, int divisions, int depth, boolean drawLines, 
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
				processArea(newBot + yScale, newBot, newLeft, newLeft + xScale, 100, depth - 1, drawLines,
						bucket.getPoints(), outPoints, outLines);
			}
		}
		
	}
	
	static private class Bucket {
		private List<TrailPointRecord> points;
		private int y;
		private int x;
		private String id;
		private double totalValues;
		private Set<String> deviceIds;
		private Set<String> connectedBucketIds; //ids of buckets that have connecting lines to this bucket
		private long oldestTime;
		private long latestTime;
		
		public Bucket(int latIndex, int lngIndex) {
			this.points = new LinkedList<TrailPointRecord>();
			this.y = latIndex;
			this.x = lngIndex;
			this.id = String.valueOf(this.y) + String.valueOf(this.x);
			this.totalValues = 0;
			this.oldestTime = 0;
			this.latestTime = Long.MAX_VALUE;
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
				if(this.oldestTime > time) {
					this.oldestTime = time;
				}
				if(this.latestTime < time) {
					this.latestTime = time;
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
		
		public String getId() {
			return this.id;
		}
		
		public Set<String> getDeviceIds() {
			return this.deviceIds;
		}
		
		public long getOldestTime() {
			return this.oldestTime;
		}
		
		public long getLatestTime() {
			return this.latestTime;
		}
		
		public void setConnected(String bucketId) {
			if(this.connectedBucketIds == null) {
				this.connectedBucketIds = new HashSet<String>(8, 1.0F);
			}
			this.connectedBucketIds.add(bucketId);
		}
		
		public boolean isConnected(String bucketId) {
			if(this.connectedBucketIds == null) {
				return false;
			}
			return this.connectedBucketIds.contains(bucketId);
		}
		
	}

}
