package com.umn.seniordesign.trailmonitor.entities.geojson;

import java.util.List;

@SuppressWarnings("rawtypes")
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
	
	/**
	 * @param geometry - A valid object with class type Geometry
	 * @param properties - A valid object with class type Properties
	 * @throws Exception Thrown unless enclosing instance has type = GeoJson.Types.Feature
	 */
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
}
