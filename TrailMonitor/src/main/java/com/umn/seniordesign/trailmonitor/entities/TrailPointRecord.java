package com.umn.seniordesign.trailmonitor.entities;

import java.util.Calendar;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.umn.seniordesign.trailmonitor.utilities.DataConverter;

@DynamoDBTable(tableName="TrailData") //controls what table record is written to
public class TrailPointRecord {
	
	private String compositeCoordinates;
	private Calendar timeStamp;
	private Long deviceId;
	private Integer coordinate; //identifies position by what square within a grid of squares partitioned by integer gps coordinates
	private Double latitude;
	private Double longitude;
	private Integer value;
	
	@DynamoDBHashKey(attributeName = "CompositeCoordinates")  
    public String getCompositeCoordinates() { return this.compositeCoordinates; }
    public void setCompositeCoordinates(String compositeCoordinates) {this.compositeCoordinates = compositeCoordinates; }
	
    @DynamoDBRangeKey(attributeName = "TimeStamp")
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "Coordinate-TimeStamp-index", attributeName="TimeStamp")
    public Calendar getTimeStamp() { return this.timeStamp; }
    public void setTimeStamp(Calendar timeStamp) { this.timeStamp = timeStamp; }
    
    @DynamoDBAttribute(attributeName = "DeviceId")
    public Long getDeviceId() { return this.deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }

    @DynamoDBAttribute(attributeName = "Coordinate")
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "Coordinate-TimeStamp-index", attributeName="Coordinate")
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
    public void setValue(Integer value) { this.value = value; }
    
    public void setAllCoordinates(Double latitude, Double longitude) {
    	this.compositeCoordinates = longitude.toString() + latitude.toString();
    	this.latitude = latitude;
    	this.longitude = longitude;
    	//generate a unique linear value for each integer latitude/longitude combination
    	this.coordinate = DataConverter.reduceCoordinateDimension(longitude, latitude);
    }
    
    public void setTimeStampFromMilli(long milliseconds) {
    	this.timeStamp = new Calendar.Builder().build();
    	this.timeStamp.setTimeInMillis(milliseconds);
    }
}
