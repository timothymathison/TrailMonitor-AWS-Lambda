package com.umn.seniordesign.trailmonitor.services;

import java.util.List;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.umn.seniordesign.trailmonitor.entities.TrailPointRecord;

public class DatabaseTask {
	
	static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	
	public static DatabaseTaskResult<Object> saveItems(List<TrailPointRecord> dataPoints) {
		try {
			DynamoDBMapper mapper = new DynamoDBMapper(client);
			mapper.batchSave(dataPoints);
		}
		catch(Exception e) {
			return new DatabaseTaskResult<Object>(false, "Database exception: " + e.getMessage(), null);
		}
		
		return new DatabaseTaskResult<Object>(true, dataPoints.size() + " data points saved", null);
	}

}
