package com.umn.seniordesign.trailmonitor.entities;

import java.util.List;

public class PostDataRequest {
	
	private String deviceId;
	private List<TrailPoint> data;
	
	public String getDeviceId() {
		return this.deviceId;
	}
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public List<TrailPoint> getData() {
		return this.data;
	}
	
	public void setData(List<TrailPoint> data) {
		this.data = data;
	}
}
