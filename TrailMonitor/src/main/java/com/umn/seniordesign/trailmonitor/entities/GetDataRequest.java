package com.umn.seniordesign.trailmonitor.entities;

import java.util.Map;

public class GetDataRequest {
	
	private Map<String, String> params;
	private String sourceIp;
	
	public Map<String, String> getParams() {
		return this.params;
	}
	
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	
	public String getSourceIp() {
		return this.sourceIp;
	}
	
	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}
	
}
