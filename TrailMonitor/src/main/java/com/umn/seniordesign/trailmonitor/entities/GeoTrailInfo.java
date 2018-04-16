package com.umn.seniordesign.trailmonitor.entities;

import java.util.Arrays;
import java.util.List;

import com.umn.seniordesign.trailmonitor.entities.geojson.GeoJsonTile;

public class GeoTrailInfo {
	private List<GeoJsonTile> tiles;
	private String zoomRange;
	long featureCount;
	
	//should always be ordered by increasing zoom values and should only overlap by one
	public static final List<String> availableZoomRanges = Arrays.asList("4-6", "6-10", "10-50"); //Mapbox zoom values
	/**
	 * @param zoom - Mapbox zoom level
	 * @return zoomDepth, which is the index in {@link #availableZoomRanges} where the range containing zoom can be found
	 */
	public static Integer getZoomDepth(double zoom) { //numbers within if statements must agree with ranges above
		if(zoom >= 4 && zoom < 6) {
			return 0;
		}
		else if(zoom >= 6 && zoom < 10) {
			return 1;
		}
		else if(zoom >= 10 && zoom < 50) {
			return 2;
		}
		else {
			return null; //zoom is not within any of the available ranges
		}
	}
	
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
