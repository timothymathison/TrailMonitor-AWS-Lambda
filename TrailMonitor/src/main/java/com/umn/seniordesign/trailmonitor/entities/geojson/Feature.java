package com.umn.seniordesign.trailmonitor.entities.geojson;

@SuppressWarnings({"rawtypes", "unused"})
public class Feature {
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