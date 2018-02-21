package com.umn.seniordesign.trailmonitor.entities;

public class PostDataResponse {
	
	private String message;
	private int status;
	private String echo; //optional - contains/echos the data that was received
	
	public PostDataResponse(int status, String message) {
		this.status = status;
		this.message = message;
		this.echo = null;
		
		//if status anything besides default (200 - OK) must through exception for API to notice
		if(status != 200) {
			throw new RuntimeException(this.toString());
		}
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public int getStatus() {
		return this.status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getEcho() {
		return this.echo;
	}
	
	public void setEcho(String data) {
		this.echo = data;
	}
	
	public String toString() {
		return "{\"message\": \"" + this.message + "\", \"status\": " + this.status + (this.echo != null ? ", \"echo\": " + this.echo : "") + "}";
	}
}
