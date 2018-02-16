package com.umn.seniordesign.trailmonitor.entities;

public class TrailPoint {
	
	private Long timeStamp; //in epoch milliseconds
	private Double latitude;
	private Double longitude;
	private Integer value; //intensity
	
	public Long getTimeStamp() {
		return this.timeStamp;
	}
	
	public void setTimeStamp(Long timeStamp) {
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
	
	public boolean valid() { //checks the validity and completeness of this object
		return this.timeStamp != null && this.latitude != null && this.longitude != null && this.value != null;
	}
	
	public String toString() {
		return "{TimeStamp : " + this.timeStamp + ", Latitude : " + this.latitude + ", Longitude : " + this.longitude + ", Value : " + this.value + "}";
	}
}
