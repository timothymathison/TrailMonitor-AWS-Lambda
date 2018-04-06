package com.umn.seniordesign.trailmonitor.entities.geojson;

import java.util.List;

import com.umn.seniordesign.trailmonitor.entities.GPSTuple;

@SuppressWarnings("rawtypes")
public class GeoJsonTile {

	private Types type;
	private List<Feature> pointData; //used when type = FeatureCollection
	private List<Feature> lineData; //used when type = FeatureCollection
	private Geometry geometry; //used when type = Feature
	private Properties properties; //used when type = Feature
	private GPSTuple<Double, Double> cornerCoordinate;
	private String zoomRange;

	public enum Types {
		FeatureCollection,
		Feature,
		//Incomplete list; can add more
	}
	
	public GeoJsonTile(Types type, GPSTuple<Double, Double> cornerCoord) {
		this.type = type;
		this.cornerCoordinate = cornerCoord;
	}
	
	public GeoJsonTile(Types type, GPSTuple<Double, Double> cornerCoord, String zoomRange) {
		this.type = type;
		this.cornerCoordinate = cornerCoord;
		this.zoomRange = zoomRange;
	}
	
	public Types getType() {
		return this.type;
	}
	
	public List<Feature> getPointData() {
		return this.pointData;
	}
	
	/**
	 * @param pointFeatures - A list of {@link #Feature} objects
	 * @throws Exception Thrown unless enclosing instance has type = GeoJsonTile.Types.FeatureCollection
	 */
	public void setPointData(List<Feature> pointFeatures) throws Exception {
		if(this.type == Types.FeatureCollection) {
			this.pointData = pointFeatures;
		}
		else {
			throw new Exception("GeoJsonTile of type " + this.type.toString() + " does not use List data of type \"Feature\"."
					+ " Try GeoJsonTile with type \"FeatureCollection\"");
		}
	}
	
	public List<Feature> getLineData() {
		return this.lineData;
	}
	
	/**
	 * @param lineFeatures - A list of {@link #Feature} objects
	 * @throws Exception Thrown unless enclosing instance has type = GeoJsonTile.Types.FeatureCollection
	 */
	public void setLineData(List<Feature> lineFeatures) throws Exception {
		if(this.type == Types.FeatureCollection) {
			this.lineData = lineFeatures;
		}
		else {
			throw new Exception("GeoJsonTile of type " + this.type.toString() + " does not use List data of type \"Feature\"."
					+ " Try GeoJsonTile with type \"FeatureCollection\"");
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
			throw new Exception("GeoJsonTile of type " + this.type.toString() + " does not use single \"Feature\" object."
					+ " Try GeoJsonTile with type \"Feature\"");
		}
	}
	
	public GPSTuple<Double,Double> getCornerCoordinate() {
		return this.cornerCoordinate;
	}
	
	public void setCornerCoordinate(GPSTuple<Double, Double> cornerCoord) {
		this.cornerCoordinate = cornerCoord;
	}
	
	public String getZoomRange() {
		return this.zoomRange;
	}
	
	public void setZoomRange(String zoomRange) {
		this.zoomRange = zoomRange;
	}
}
