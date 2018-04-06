package com.umn.seniordesign.trailmonitor.entities;

public class GPSTuple<X, Y> {
	public final X lng;
	public final Y lat;
	
	public GPSTuple(X lng, Y lat) {
		this.lng = lng;
		this.lat = lat;
	}
	
	@SuppressWarnings("unchecked")
	public GPSTuple(X lng, long lat) {
		this.lng = lng;
		this.lat = (Y)Long.valueOf(lat);
	}
	
	@SuppressWarnings("unchecked")
	public GPSTuple(long lng, Y lat) {
		this.lng = (X)Long.valueOf(lng);
		this.lat = lat;
	}
	
	@SuppressWarnings("unchecked")
	public GPSTuple(long lng, long lat) {
		this.lng = (X)Long.valueOf(lng);
		this.lat = (Y)Long.valueOf(lat);
	}
}
