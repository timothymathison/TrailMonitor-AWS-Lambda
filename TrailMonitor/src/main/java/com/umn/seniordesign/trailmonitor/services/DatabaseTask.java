package com.umn.seniordesign.trailmonitor.services;

import java.util.List;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.umn.seniordesign.trailmonitor.entities.TrailPointRecord;

public class DatabaseTask {
	
	static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	
	/**
	 * <h1>Saves List of TrailPointRecord objects</h1>
	 * @param dataPoints - List of TrailPointRecord class objects
	 * @return DatabaseTaskResult class object indicating success and containing return data (if any)
	 */
	public static DatabaseTaskResult<Object> saveItems(List<TrailPointRecord> dataPoints) {
		try {
			DynamoDBMapper mapper = new DynamoDBMapper(client);
			mapper.batchSave(dataPoints);
		}
		catch(Exception e) { //error encountered when interfacing with AWS DynamoDB service
			return new DatabaseTaskResult<Object>(false, "Database exception: " + e.getMessage(), null);
		}
		
		return new DatabaseTaskResult<Object>(true, dataPoints.size() + " data points saved", null);
	}

}
