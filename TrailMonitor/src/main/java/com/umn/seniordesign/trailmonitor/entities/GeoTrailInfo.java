package com.umn.seniordesign.trailmonitor.entities;

import java.util.Arrays;
import java.util.List;

import com.umn.seniordesign.trailmonitor.entities.geojson.GeoJsonTile;

public class GeoTrailInfo {
	private List<GeoJsonTile> tiles;
	private String zoomRange;
	private static final List<String> availableZoomRanges = Arrays.asList("9-20", "6-9", "2-6");
	
	public GeoTrailInfo() {
		this.tiles = null;
		this.zoomRange = null;
	}
	
	public GeoTrailInfo(String zoomSpan, List<GeoJsonTile> tiles) {
		this.zoomRange = zoomSpan;
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
