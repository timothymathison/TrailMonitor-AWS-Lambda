package com.umn.seniordesign.trailmonitor.entities;

import java.util.HashMap;
import java.util.Map;

public class GetDataResponce<datatype> {
	
	private static final Map<Integer, String> statuses = new HashMap<Integer, String>();
	static {
		statuses.put(200, "200 OK");
		statuses.put(204, "204 No Content");
		statuses.put(400, "400 Bad Request");
		statuses.put(500, "500 Internal Server Error");
	}
	
	private String status;
	private String type;
	private datatype data;
	
	public String getStatus() {
		return this.status;
	}
	
	public void setStatus(int statusCode) {
		this.status = statuses.get(statusCode);
	}
	
	public String getType() {
		return this.type;
	}
	
	public datatype getData() {
		return this.data;
	}
	
	public void setData(datatype data) {
		this.data = data;
	}

}
