package com.umn.seniordesign.trailmonitor.entities;

public class TrailPoint {

	public TrailPoint(Long timeStamp, Double latitude, Double longitude, Integer value) {
		this.timeStamp = timeStamp;
		this.latitude = latitude;
		this.longitude = longitude;
		this.value = value;
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
		return this.latitude;
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
}
