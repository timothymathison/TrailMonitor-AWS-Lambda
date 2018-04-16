package com.umn.seniordesign.trailmonitor.entities;

public class GPSTuple {
	public final double lng;
	public final double lat;
	
	public GPSTuple(double lng, double lat) {
		this.lng = lng;
		this.lat = lat;
	}
	
	public GPSTuple(double lng, long lat) {
		this.lng = lng;
		this.lat = Long.valueOf(lat).doubleValue();
	}
	
	public GPSTuple(long lng, Double lat) {
		this.lng = Long.valueOf(lng).doubleValue();
		this.lat = lat;
	}

	public GPSTuple(long lng, long lat) {
		this.lng = Long.valueOf(lng).doubleValue();
		this.lat = Long.valueOf(lat).doubleValue();
	}
}
