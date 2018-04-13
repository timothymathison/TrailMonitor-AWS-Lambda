package com.umn.seniordesign.trailmonitor.utilities;

import java.math.BigDecimal;
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
import com.umn.seniordesign.trailmonitor.entities.Tuple;
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
		GeoTrailInfo geoTrailInfo = new GeoTrailInfo(GeoTrailInfo.availableZoomRanges.get(0), geoJsonTiles, featureCount);
		
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
		if(zoomDepth == -1) {
			return build(tileRecords); //call other build method, which returns un-processed data
		}
		
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
			throw new Exception("Invalid zoomDepth sent to GeoTrailInfo build function, must be in range 0-2");
		}
		String zoomRange = GeoTrailInfo.availableZoomRanges.get(zoomDepth);
		
		//declarations for iterating
		Map.Entry<Integer,List<TrailPointRecord>> tileRecord;
		Iterator<Map.Entry<Integer,List<TrailPointRecord>>> tileIterator = tileRecords.entrySet().iterator();
		
		List<GeoJsonTile> geoJsonTiles = new LinkedList<GeoJsonTile>(); //list of GeoJsonTiles that will be returned
		
		//temp variable declarations
		GeoJsonTile tile;
		GPSTuple tileCorner;
		//lists of features to be plotted
		List<Feature> pointFeatures;
		List<Feature> lineFeatures;
		long featureCount = 0;
		while(tileIterator.hasNext()) {
			tileRecord = tileIterator.next();
			tileCorner = DataConverter.expandCoordinateDimension(tileRecord.getKey());
			//create new tile, type FeatureCollection
			tile = new GeoJsonTile(GeoJsonTile.Types.FeatureCollection, tileCorner, zoomRange);
			pointFeatures = new LinkedList<Feature>();
			lineFeatures = new LinkedList<Feature>();
			if(tileRecord.getValue().size() > 0) {
				//get top-bottom-left-right
				// using big decimal to maintain accuracy when top-bottom-left-right are calculated recursively
				BigDecimal top = new BigDecimal(tileCorner.lat + 1);
				BigDecimal bot = new BigDecimal(tileCorner.lat);
				BigDecimal left = new BigDecimal(tileCorner.lng);
				BigDecimal right = new BigDecimal(tileCorner.lng + 1);
				//process tile
				processArea(top, bot, left, right, divisions, startDepth, computeLines, tileRecord.getValue(), pointFeatures, lineFeatures);
				featureCount += pointFeatures.size() + lineFeatures.size();
			}
			tile.setPointData(pointFeatures);
			tile.setLineData(lineFeatures);
			geoJsonTiles.add(tile);
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
	public static void processArea(BigDecimal top, BigDecimal bot, BigDecimal left, BigDecimal right, int divisions, int depth, boolean drawLines, 
			List<TrailPointRecord> points, List<Feature> outPoints, List<Feature> outLines) throws Exception {
		
		Bucket[][] grid = new Bucket[divisions][divisions]; //rectangular grid that all points will be mapped into
		List<Bucket> populatedBuckets = new LinkedList<Bucket>(); //list of non-empty buckets that have points
		Iterator<TrailPointRecord> iterRawPoints = points.iterator();
		
		//must use big decimal format when dividing by fractions to avoid rounding error
		//(right - left) / divisions
		BigDecimal xScale = right.subtract(left).divide(new BigDecimal(divisions)); //longitude-distance / division
		//(top - bot) / divisions
		BigDecimal yScale = top.subtract(bot).divide(new BigDecimal(divisions)); //latitude-distance / division
		
		int y = 0;
		int x = 0;
		while(iterRawPoints.hasNext()) { //place each point in the appropriate bucket based on it's lat and lng
			TrailPointRecord point = iterRawPoints.next();
			
			//y = (int)Math.floor((point.getLatitude() - bot) / yScale);
			//x = (int)Math.floor((point.getLongitude() - left) / xScale);
			try {
				y = (int)Math.floor(new BigDecimal(point.getLatitude()).subtract(bot)
						.divide(yScale).doubleValue());
				x = (int)Math.floor(new BigDecimal(point.getLongitude()).subtract(left)
						.divide(xScale).doubleValue());
				
				if(grid[y][x] == null) { //if bucket is empty/null create new bucket at this position
					grid[y][x] = new Bucket(y, x);
					populatedBuckets.add(grid[y][x]);
				}
				
				grid[y][x].add(point, depth <= 0); //add point to bucket
			}
			catch(ArrayIndexOutOfBoundsException | ArithmeticException e) {
				throw new Exception("Error mapping points into bucket grid: " + e.getMessage() +
						", bot: " + bot + ", left: " + left + ", yScale" + yScale + ", xScale: " + xScale + 
						", lat: " + point.getLatitude() + ", long: " + point.getLongitude());
			}
		}
		
		Iterator<Bucket> buckIter = populatedBuckets.iterator();
		Bucket bucket;
		if(depth <= 0) { //base/deepest depth - compute lines
			GPSTuple center;
			Geometry<Double> geometry;
			Properties properties;
			Feature feature;
			while(buckIter.hasNext()) {
				bucket = buckIter.next();
				center = bucket.getAvgCenter();
				
				//compute combined points
				geometry = new Geometry<Double>(Arrays.asList(center.lng, center.lat));
				properties = new Properties(bucket.getValue(), bucket.size(), bucket.getDeviceIds(), bucket.getOldestTime());
				feature = new Feature(geometry, properties);
				outPoints.add(feature);
				
				//compute lines
				if(drawLines) {
					Tuple coord;
					feature = null;
					//search for points to connect to in circle of buckets around current bucket
					GridIterator iter = new GridIterator(divisions, divisions, bucket.getX(), bucket.getY());
					while(iter.hasNext()) {
						coord = iter.next();
						if(coord != null) {
							feature = bucket.connectBucket(grid[coord.y][coord.x]);
							if(feature != null) {
								outLines.add(feature);
							}
						}
					}
					if(feature == null) {// no adjacent buckets were found to connect to
						iter.increaseRadius();
						while(iter.hasNext()) {
							coord = iter.next();
							if(coord != null) {
								feature = bucket.connectBucket(grid[coord.y][coord.x]);
								if(feature != null) {
									outLines.add(feature);
								}
							}
						}
					}
				}
			}	
		}
		else {
			BigDecimal newBot;
			BigDecimal newLeft;
			while(buckIter.hasNext()) {
				bucket = buckIter.next();
				//newBot = bot + bucket.getY() * yScale;
				newBot = bot.add(new BigDecimal(bucket.getY()).multiply(yScale));
				//newLeft = left + bucket.getX() * xScale;
				newLeft = left.add(new BigDecimal(bucket.getX()).multiply(xScale));
				processArea(newBot.add(yScale), newBot, newLeft, newLeft.add(xScale), 100, depth - 1, drawLines,
						bucket.getPoints(), outPoints, outLines);
			}
		}
	}
	
	static private class Bucket {
		private List<TrailPointRecord> points;
		private int y;
		private int x;
		private String id;
		private double totalLat;
		private double totalLng;
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
			this.totalLat = 0;
			this.totalLng = 0;
			this.totalValues = 0;
			this.oldestTime = Long.MAX_VALUE;
			this.latestTime = 0;
		}
		
		public void add(TrailPointRecord point, boolean process) {
			this.points.add(point);
			if(process) { //combine information from all points in bucket
				this.totalLat += point.getLatitude();
				this.totalLng += point.getLongitude();
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
		
		public GPSTuple getAvgCenter() {
			return new GPSTuple(this.totalLng / this.size(), this.totalLat / this.size());
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
		
		public void setConnected(Bucket bucket) {
			if(this.connectedBucketIds == null) {
				this.connectedBucketIds = new HashSet<String>(8, 1.0F);
			}
			this.connectedBucketIds.add(bucket.getId());
			if(bucket.connectedBucketIds == null) {
				bucket.connectedBucketIds = new HashSet<String>(8, 1.0F);
			}
			bucket.connectedBucketIds.add(this.id);
		}
		
		public boolean isConnected(Bucket bucket) {
			if(this.connectedBucketIds == null) {
				return false;
			}
			return this.connectedBucketIds.contains(bucket.getId());
		}
		
		/**
		 * <h1>Determines if this bucket and another bucket should be connected, based on the points contained within each</h2>
		 * @param otherBucket - other bucket to consider connecting
		 * @return GeoJson {@link #Feature} linestring object which connects both buckets, or null if not to be connected
		 * @throws Exception if an error occurs building GeoJson
		 */
		public Feature connectBucket(Bucket otherBucket) throws Exception {
			if(otherBucket != null && !this.isConnected(otherBucket)) {
				Iterator<String> iterator = this.deviceIds.iterator();
				Set<String> otherBuckDeviceIds = otherBucket.getDeviceIds();
				while(iterator.hasNext()) {
					if(otherBuckDeviceIds.contains(iterator.next()) //this bucket and other bucket have points from same device
							/*&& this.getOldestTime() <= otherBucket.getLatestTime() && this.getLatestTime() >= otherBucket.getOldestTime()*/) { 
						GPSTuple thisCenter = this.getAvgCenter();
						GPSTuple otherCenter = otherBucket.getAvgCenter();
						Geometry<List<Double>> geometry = new Geometry<List<Double>>(Arrays.asList(Arrays.asList(thisCenter.lng, thisCenter.lat),
								Arrays.asList(otherCenter.lng, otherCenter.lat)));
						Properties properties = new Properties((this.getValue() + otherBucket.getValue()) / 2,
								(this.size() + otherBucket.size()) / 2, combineDeviceIds(this.getDeviceIds(), otherBucket.getDeviceIds()), 
								Long.max(this.getOldestTime(), otherBucket.getOldestTime()));
						Feature feature = new Feature(geometry, properties);
						
						this.setConnected(otherBucket);
						return feature;
					}
				}
			}
			return null;
		}
		
		/**
		 * <h1>Takes two sets of device ids and returns a new set containing device ids from both, without permuting either
		 * original set</h1>
		 * @param arg0 - A set of inner type String
		 * @param arg1 - A set of inner type String
		 * @return Set<String> object containing all deviceIds
		 */
		public static Set<String> combineDeviceIds(Set<String> arg0, Set<String> arg1) {
			Set<String> ids = new HashSet<String>(arg0.size() + arg1.size(), 1.0F);
			Iterator<String> iterator = arg0.iterator();
			while(iterator.hasNext()) {
				ids.add(iterator.next());
			}
			iterator = arg1.iterator();
			while(iterator.hasNext()) {
				ids.add(iterator.next());
			}
			return ids;
		}
	}
	
	static class GridIterator {
		int x;
		int y;
		int startX;
		int startY;
		int gridWidth;
		int gridHeight;
		int radius;
		
		/**
		 * <h1>Acts as an iterator returning grid coordinates which move in a circle around starting coordinate</h1>
		 * @param gridWidth - number of units across width of grid
		 * @param gridHeight - number of units across height of grid
		 * @param startX - center horizontal coordinate
		 * @param startY - center vertical coordinate
		 */
		public GridIterator(int gridWidth, int gridHeight, int startX, int startY) {
			this.startX = startX;
			this.startY = startY;
			this.x = startX - 1; //start in lower left corner
			this.y = startY - 1;
			this.gridWidth = gridWidth;
			this.gridHeight = gridHeight;
			radius = 1;
		}
		
		public boolean hasNext() {
			return !(this.x == this.startX && this.y == this.startY);
		}
		
		public Tuple next() {
			Tuple nxt = null;
			if(this.x >= 0 && this.x < this.gridWidth && this.y >= 0 && this.y < this.gridHeight) { //if within bounds of grid
				nxt = new Tuple(this.x, this.y);
			}
			
			if(this.x == this.startX && this.y < this.startY) { //reached end point directly below starting square
				this.y = this.startY; //next is the starting position
			}
			else if(this.x < this.startX && this.y < this.startY + this.radius) { //on left side
				this.y++; //move up
			}
			else if(this.y > this.startY && this.x < this.startX + this.radius) { //on top
				this.x++; //move right
			}
			else if(this.x > this.startX && this.y > this.startY - this.radius) { //on right side
				this.y--; //move down
			}
			else { //on bottom side
				this.x--; //love left
			}
			return nxt;
		}
		
		/**
		 * <h1>Increase radius of iterator</h1>
		 * Subsequent calls to next() will move at a greater radius about the center
		 */
		public void increaseRadius() {
			this.radius++;
			this.y = this.startY - this.radius; //start radius steps down
			this.x = this.startX - 1; //start one step left of horizontal center (startX)
		}
	}
}
