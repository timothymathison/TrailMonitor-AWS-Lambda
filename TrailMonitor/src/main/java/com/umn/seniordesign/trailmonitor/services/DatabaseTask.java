package com.umn.seniordesign.trailmonitor.services;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.umn.seniordesign.trailmonitor.entities.TrailPointRecord;
import com.umn.seniordesign.trailmonitor.utilities.DataConverter;

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
	
	/**
	 * 
	 * @param tileCoordinates - List of integer tile identifiers calculated by {@link #DataConverter.reduceCoordinateDimension}
	 * @return DatabaseTaskResult class object indicating success and containing return data (if any)
	 */
	public static DatabaseTaskResult<List<TrailPointRecord>> readItems(List<Integer> tileCoordinates, Calendar startTime) {
		List<TrailPointRecord> items = new LinkedList<TrailPointRecord>();
		try {
			DynamoDBMapper mapper = new DynamoDBMapper(client);
			
			DynamoDBQueryExpression<TrailPointRecord> queryExpression;
			Map<String, AttributeValue> queryAttributes;
			Map<String, String> dynamoDBReservedExpression = new HashMap<>();
			dynamoDBReservedExpression.put("#time", "TimeStamp"); //TimeStamp is a reserved keywords so use an alias
			
			Iterator<Integer> tileIterator = tileCoordinates.iterator();
			while(tileIterator.hasNext()) { //iterate through and query for each coordinate tile requested
				queryAttributes = new HashMap<String, AttributeValue>();
				queryAttributes.put(":coord", new AttributeValue().withN(tileIterator.next().toString())); //coordinate tile to match
				queryAttributes.put(":startTime", new AttributeValue().withS(DataConverter.timeStamp(startTime))); //start time to filter by
				
				queryExpression = new DynamoDBQueryExpression<TrailPointRecord>()
						.withIndexName("Coordinate-TimeStamp-index") //query from index based on linear (1-dimensional) Coordinate
						.withConsistentRead(false)
						.withKeyConditionExpression("Coordinate = :coord and #time >= :startTime")
						.withExpressionAttributeValues(queryAttributes)
						.withExpressionAttributeNames(dynamoDBReservedExpression);
				
				items.addAll(mapper.query(TrailPointRecord.class, queryExpression));
			}	
			
		}
		catch(Exception e) { //error encountered when interfacing with AWS DynamoDB service
			return new DatabaseTaskResult<List<TrailPointRecord>>(false, "Database exception: " + e.getMessage(), null);
		}
		
		
		return new DatabaseTaskResult<List<TrailPointRecord>>(true, items.size() + " trail records retrieved", items);
	}

}
