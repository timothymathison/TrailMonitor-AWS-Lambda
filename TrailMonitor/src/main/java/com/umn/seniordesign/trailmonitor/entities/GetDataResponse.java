package com.umn.seniordesign.trailmonitor.entities;

import java.util.HashMap;
import java.util.Map;

public class GetDataResponse<datatype> {
	
	private static final Map<Integer, String> statuses = new HashMap<Integer, String>();
	static {
		statuses.put(200, "200 OK");
		statuses.put(204, "204 No Content");
		statuses.put(400, "400 Bad Request");
		statuses.put(500, "500 Internal Server Error");
	}
	
	private String status;
	private String message;
	private String type;
	private datatype data;
	
	public GetDataResponse(int statusCode, String message, datatype data) {
		this.status = statuses.get(statusCode);
		this.message = message;
		this.data = data;
		
		this.type = data.getClass().getSimpleName();
		
		//if status anything besides default (200 - OK) must through exception for API to notice
		if(statusCode != 200) {
			throw new RuntimeException(this.toString());
		}
	}
	
	public String getStatus() {
		return this.status;
	}
	
	public void setStatus(int statusCode) {
		this.status = statuses.get(statusCode);
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void setMessage(String message) {
		this.message = message;
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

	public String toString() {
		return "{\"message\": \"" + this.message + "\", \"status\": \"" + this.status + "\", \"type\": \"" + this.type + "\""
				+ (this.data != null ? ", \"data\": " + this.data.toString() : "") + "}";
	}
}
