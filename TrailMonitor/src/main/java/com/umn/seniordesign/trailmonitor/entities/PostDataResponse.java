package com.umn.seniordesign.trailmonitor.entities;

public class PostDataResponse {
	
	private String message;
	private int status;
	private String echo; //optional - contains/echos the data that was received
	
	public PostDataResponse(int status, String message) {
		this.status = status;
		this.message = message;
		this.echo = null;
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
}
