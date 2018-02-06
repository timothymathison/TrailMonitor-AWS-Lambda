package com.umn.seniordesign.trailmonitor.entities;

import java.util.List;

public class PostDataRequest {
	
	public PostDataRequest() {
	}
	
	private Long deviceId;
	private List<TrailPoint> data;
	
	public Long getDeviceId() {
		return this.deviceId;
	}
	
	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}
	
	public List<TrailPoint> getData() {
		return this.data;
	}
	
	public void setData(List<TrailPoint> data) {
		this.data = data;
	}
}
