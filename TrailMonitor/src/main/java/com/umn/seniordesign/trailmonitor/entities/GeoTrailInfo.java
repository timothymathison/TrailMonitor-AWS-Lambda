package com.umn.seniordesign.trailmonitor.entities;

import java.util.Arrays;
import java.util.List;

import com.umn.seniordesign.trailmonitor.entities.geojson.GeoJsonTile;

public class GeoTrailInfo {
	private List<GeoJsonTile> tiles;
	private String zoomRange;
	public static final List<String> availableZoomRanges = Arrays.asList("2-6", "6-10", "10-20");
	
	public GeoTrailInfo() {
		this.tiles = null;
		this.zoomRange = null;
	}
	
	public GeoTrailInfo(List<GeoJsonTile> tiles) {
		this.tiles = tiles;
		this.zoomRange = null;
	}
	
	public GeoTrailInfo(String zoomRange, List<GeoJsonTile> tiles) {
		this.zoomRange = zoomRange;
		this.tiles = tiles;
	}
	
	public List<GeoJsonTile> getTiles() {
		return this.tiles;
	}
	
	public String getZoomRange() {
		return this.zoomRange;
	}
	
	public List<String> getAvailableZoomRanges() {
		return availableZoomRanges;
	}
}
