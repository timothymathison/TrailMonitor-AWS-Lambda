package com.umn.seniordesign.trailmonitor.entities.geojson;

public class Properties {
	private int value;
	private int deviceId;
	private long timeStamp;
	//more fields can be added (customizable)
	
	public Properties(int value, int deviceId, long timeStamp) {
		this.value = value;
		this.deviceId = deviceId;
		this.timeStamp = timeStamp;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public int getDeviceId() {
		return this.deviceId;
	}
	
	public long getTimeStamp() {
		return this.timeStamp;
	}
}