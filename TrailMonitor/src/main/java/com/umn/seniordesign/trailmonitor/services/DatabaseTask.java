package com.umn.seniordesign.trailmonitor.services;

import java.util.List;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper.FailedBatch;
import com.umn.seniordesign.trailmonitor.entities.TrailPointRecord;

public class DatabaseTask {
	
	static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	
	public static void saveItems(List<TrailPointRecord> dataPoints) {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		//TODO: check for/handle errors
		mapper.batchSave(dataPoints);
	}

}
