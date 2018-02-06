package com.umn.seniordesign.trailmonitor.entities;

import java.util.Calendar;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="TrailData_Beta")
public class TrailPointRecord {
	
	private String compositeCoordinates;
	private Calendar timeStamp;
	private Integer deviceId;
	private Integer coordinate; //identifies position within a grid of squares divided up by integer gps coordinates
	private Double latitude;
	private Double longitude;
	private Integer value;
	
	@DynamoDBHashKey(attributeName = "CompositeCoordinates")  
    public String getCompositeCoordinates() { return this.compositeCoordinates; }
    public void setCompositeCoordinates(String compositeCoordinates) {this.compositeCoordinates = compositeCoordinates; }
	
    @DynamoDBRangeKey(attributeName = "TimeStamp")
    public Calendar getTimeStamp() { return this.timeStamp; }
    public void setTimeStamp(Calendar timeStamp) { this.timeStamp = timeStamp; }
    
    @DynamoDBAttribute(attributeName = "DeviceId")
    public Integer getDeviceId() { return this.deviceId; }
    public void setDeviceId(Integer deviceId) { this.deviceId = deviceId; }
    
    @DynamoDBAttribute(attributeName = "Coordinate")
    public Integer getCoordinate() { return this.coordinate; }
    public void setCoordinate(Integer coordinate) { this.coordinate = coordinate; }
    
    @DynamoDBAttribute(attributeName = "Latitude")
    public Double getLatitude() { return this.latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    @DynamoDBAttribute(attributeName = "Longitude")
    public Double getLongitude() { return this.longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    
    @DynamoDBAttribute(attributeName = "Value")
    public Integer getValue() { return this.value; }
    public void setValue(int value) { this.value = value; }
    
    public void setAllCoordinates(Double latitude, Double longitude) {
    	this.compositeCoordinates = latitude.toString() + longitude.toString();
    	this.latitude = latitude;
    	this.longitude = longitude;
    	this.coordinate = ((int)Math.floor(longitude)) * 200 + ((int)Math.floor(latitude));
    }
    
    public void setTimeStampFromMilli(long milliseconds) {
    	this.timeStamp = new Calendar.Builder().build();
    	this.timeStamp.setTimeInMillis(milliseconds);
    }
}
