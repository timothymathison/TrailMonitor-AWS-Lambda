package com.umn.seniordesign.trailmonitor.entities;

public class GPSTuple<X, Y> {
	public final X lng;
	public final Y lat;
	
	public GPSTuple(X lng, Y lat) {
		this.lng = lng;
		this.lat = lat;
	}
}
