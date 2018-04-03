package com.umn.seniordesign.trailmonitor.entities.geojson;

import java.util.HashSet;
import java.util.Set;

public class Properties {
	private int value;
	private Set<String> deviceIds;
	private long timeStamp;
	//more fields can be added (customizable)
	
	public Properties(int value, String deviceId, long timeStamp) {
		this.value = value;
		this.deviceIds = new HashSet<String>(1, 1.0F);
		this.deviceIds.add(deviceId);
		this.timeStamp = timeStamp;
	}
	
	public Properties(int value, Set<String> deviceIds, long timeStamp) {
		this.value = value;
		this.deviceIds = deviceIds;
		this.timeStamp = timeStamp;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public Set<String> getDeviceIds() {
		return this.deviceIds;
	}
	
	public long getTimeStamp() {
		return this.timeStamp;
	}
}