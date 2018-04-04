package com.umn.seniordesign.trailmonitor.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.umn.seniordesign.trailmonitor.entities.geojson.GeoJson;

public class GeoData {
	private GeoJson pointData;
	private GeoJson lineData;
	private String zoomSpan;
	private static final List<String> zoomSpans = Arrays.asList("9-20", "6-9", "4-6");
	
	public GeoData(String zoomSpan, GeoJson pointData, GeoJson lineData) {
		this.zoomSpan = zoomSpan;
		this.pointData = pointData;
		this.lineData = lineData;
	}
	
	public String getZoomSpan() {
		return this.zoomSpan;
	}
	
	public GeoJson getPointData() {
		return this.pointData;
	}
	
	public GeoJson getLineData() {
		return this.lineData;
	}
	
	public List<String> getZoomSpans() {
		return zoomSpans;
	}
}
