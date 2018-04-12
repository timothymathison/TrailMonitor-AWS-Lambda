package com.umn.seniordesign.trailmonitor.entities;

import java.util.Arrays;
import java.util.List;

import com.umn.seniordesign.trailmonitor.entities.geojson.GeoJsonTile;

public class GeoTrailInfo {
	private List<GeoJsonTile> tiles;
	private String zoomRange;
	long featureCount;
	public static final List<String> availableZoomRanges = Arrays.asList("4-6", "6-10", "10-20"); //Mapbox zoom values
	
	public GeoTrailInfo() {
		this.tiles = null;
		this.zoomRange = null;
		this.featureCount = 0;
	}
	
	public GeoTrailInfo(List<GeoJsonTile> tiles, long featureCount) {
		this.tiles = tiles;
		this.zoomRange = null;
		this.featureCount = featureCount;
	}
	
	public GeoTrailInfo(String zoomRange, List<GeoJsonTile> tiles, long featureCount) {
		this.zoomRange = zoomRange;
		this.tiles = tiles;
		this.featureCount = featureCount;
	}
	
	public List<GeoJsonTile> getTiles() {
		return this.tiles;
	}
	
	public String getZoomRange() {
		return this.zoomRange;
	}
	
	public long getFeatureCount() {
		return this.featureCount;
	}
	
	public List<String> getAvailableZoomRanges() {
		return availableZoomRanges;
	}
}
