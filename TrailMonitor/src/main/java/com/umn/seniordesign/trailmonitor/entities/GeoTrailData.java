package com.umn.seniordesign.trailmonitor.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.umn.seniordesign.trailmonitor.entities.geojson.GeoJsonTile;

public class GeoTrailData {
	private GeoJsonTile pointData;
	private GeoJsonTile lineData;
	private String zoomSpan;
	private static final List<String> zoomSpans = Arrays.asList("9-20", "6-9", "4-6");
	
	public GeoTrailData(String zoomSpan, GeoJsonTile pointData, GeoJsonTile lineData) {
		this.zoomSpan = zoomSpan;
		this.pointData = pointData;
		this.lineData = lineData;
	}
	
	public String getZoomSpan() {
		return this.zoomSpan;
	}
	
	public GeoJsonTile getPointData() {
		return this.pointData;
	}
	
	public GeoJsonTile getLineData() {
		return this.lineData;
	}
	
	public List<String> getZoomSpans() {
		return zoomSpans;
	}
}
