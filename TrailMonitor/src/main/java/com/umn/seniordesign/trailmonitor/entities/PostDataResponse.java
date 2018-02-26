package com.umn.seniordesign.trailmonitor.entities;

import java.util.HashMap;
import java.util.Map;

public class PostDataResponse {
	
	private static final Map<Integer, String> statuses = new HashMap<Integer, String>();
	static {
		statuses.put(200, "200 OK");
		statuses.put(204, "204 No Content");
		statuses.put(400, "400 Bad Request");
		statuses.put(500, "500 Internal Server Error");
	}
			
	private String message;
	private String status;
	private String echo; //optional - contains/echos the data that was received
	
	public PostDataResponse(int statusCode, String message) {
		this.status = statuses.get(statusCode);
		this.message = message;
		this.echo = null;
		
		//if status anything besides default (200 - OK) must through exception for API to notice
		if(statusCode != 200) {
			throw new RuntimeException(this.toString());
		}
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getStatus() {
		return this.status;
	}
	
	public void setStatus(int statusCode) {
		this.status = statuses.get(statusCode);
	}
	
	public String getEcho() {
		return this.echo;
	}
	
	public void setEcho(String data) {
		this.echo = data;
	}
	
	public String toString() {
		return "{\"message\": \"" + this.message + "\", \"status\": \"" + this.status + "\"" 
				+ (this.echo != null ? ", \"echo\": " + this.echo : "") + "}";
	}
}
