package com.umn.seniordesign.trailmonitor.entities;

import java.util.List;

//TODO: Document GeoJson class

@SuppressWarnings({"rawtypes", "unused"})
public class GeoJson {

	private Types type;
	private List<Feature> features; //used when type = FeatureCollection
	private Geometry geometry; //used when type = Feature
	private Properties properties; //used when type = Feature 
	
	public GeoJson(Types type) {
		this.type = type;
	}
	
	public Types getType() {
		return this.type;
	}
	
	public List<Feature> getFeatures() {
		return this.features;
	}
	
	/**
	 * @param features - A list of {@link #features} objects
	 * @throws Exception Thrown unless enclosing instance has type = GeoJson.Types.FeatureCollection
	 */
	public void setFeatures(List<Feature> features) throws Exception {
		if(this.type == Types.FeatureCollection) {
			this.features = features;
		}
		else {
			throw new Exception("GeoJson of type " + this.type.toString() + " does not use List of type \"Feature\"."
					+ " Try GeoJson with type \"FeatureCollection\"");
		}
	}
	
	public Feature getFeature() {
		return new Feature(this.geometry, this.properties);
	}
	
	public void setFeature(Geometry geometry, Properties properties) throws Exception {
		if(this.type == Types.Feature) {
			this.geometry = geometry;
			this.properties = properties;
		}
		else {
			throw new Exception("GeoJson of type " + this.type.toString() + " does not use single \"Feature\" object."
					+ " Try GeoJson with type \"Feature\"");
		}
	}
	
	public enum Types {
		FeatureCollection,
		Feature,
		//Incomplete list; can add more
	}
	
	public enum GeometryTypes {
		Point,
		MultiPoint,
		LineString, //un-implemented
		//Incomplete list; can add more
	}
	
	public static class Feature {
		private final String type = "Feature";
		private Properties properties;
		private Geometry geometry;
		
		public Feature(Geometry geometry, Properties properties) {
			this.properties = properties;
			this.geometry = geometry;
		}
		
		public Properties getProperties() {
			return this.properties;
		}
		
		public Geometry getGeometry() {
			return this.geometry;
		}
		
	}
	
	public static class Properties {
		private int value;
		private int deviceId;
		private long timeStamp;
		//more fields can be added (customizable)
		
		public Properties(int value, int deviceId, long timeStamp) {
			this.value = value;
			this.deviceId = deviceId;
			this.timeStamp = timeStamp;
		}
		
		public int getValue() {
			return this.value;
		}
		
		public int getDeviceId() {
			return this.deviceId;
		}
		
		public long getTimeStamp() {
			return this.timeStamp;
		}
	}
	
	/**
	 * @param <coordinateType> - Can be Double or List<Double>
	 */
	public static class Geometry<coordinateType> {
		private GeometryTypes type;
		private List<coordinateType> coordinates;
		
		/**
		 * <h1>Instantiates a new GeoJson.Geometry object if coordinates are a valid class type</h1>
		 * @param coordinates - List containing class type Double or nested List<Double>. Innermost List should be length 2, element1: <longitude>, element2: <latitude>
		 * @throws Exception Thrown if coordinates are empty or don't contain one of the two supported types
		 */
		public Geometry(List<coordinateType> coordinates) throws Exception {
			if(coordinates.size() == 0) {
				throw new Exception("Empty geometry coordinates list is prohibited");
			}
			this.coordinates = coordinates;
			if(coordinates.get(0).getClass() == Double.class) {
				this.type = GeometryTypes.Point;
				//TODO: validate coordinates
			}
			else if(coordinates.get(0).getClass() == List.class && ((List)coordinates.get(0)).get(0) == Double.class) {
				this.type = GeometryTypes.MultiPoint;
				//TODO: validate coordinates
			}
			else {
				throw new Exception("Un-supported type for GeoJson.Geometry object");
			}
		}
		
		public GeometryTypes getType() {
			return this.type;
		}
		
		public List<coordinateType> getCoordinates() {
			return this.coordinates;
		}
	}
}
