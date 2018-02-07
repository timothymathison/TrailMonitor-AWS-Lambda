package com.umn.seniordesign.trailmonitor.services;

public class DatabaseTaskResult<datatype> {
	private boolean success;
	private String message;
	private datatype data;
	
	public DatabaseTaskResult(boolean success, String message, datatype data) {
		this.success = success;
		this.message = message;
		this.data = data;
	}
	
	public boolean isSuccess() {
		return this.success;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public datatype getData() {
		return this.data;
	}
}
