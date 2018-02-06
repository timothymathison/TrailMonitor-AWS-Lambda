package com.umn.seniordesign.trailmonitor.entities;

public class TrailPoint {

	public TrailPoint() {
	}
	
	private Long timeStamp;
	private Double latitude;
	private Double longitude;
	private Integer value;
	
	public Long getTimeStamp() {
		return this.timeStamp;
	}
	
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public Double getLatitude() {
		return this.latitude;
	}
	
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	public Double getLongitude() {
		return this.longitude;
	}
	
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	public Integer getValue() {
		return this.value;
	}
	
	public void setValue(Integer value) {
		this.value = value;
	}
	
	public String toString() {
		return "{TimeStamp=" + this.timeStamp + ", Latitude=" + this.latitude + ", Longitude=" + this.longitude + ", Value=" + this.value + "}";
	}
}
