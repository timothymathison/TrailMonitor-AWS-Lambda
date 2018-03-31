package com.umn.seniordesign.trailmonitor.services;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.umn.seniordesign.trailmonitor.entities.TrailPointRecord;
import com.umn.seniordesign.trailmonitor.utilities.DataConverter;

public class DatabaseTask {
	
	static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	
	/**
	 * <h1>Saves List of TrailPointRecord objects</h1>
	 * @param dataPoints - List of TrailPointRecord class objects
	 * @return DatabaseTaskResult class object indicating success and containing return data (if any)
	 */
	public static DatabaseTaskResult<Object> savePoints(List<TrailPointRecord> dataPoints) {
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
	public static DatabaseTaskResult<Map<Integer, List<TrailPointRecord>>> readPoints(List<Integer> tileCoordinates, Calendar startTime, Context context) {
		Map<Integer, List<TrailPointRecord>> tileItems = new LinkedHashMap<Integer, List<TrailPointRecord>>();
		Long totalPoints = 0L;
		try {
			DynamoDBMapper mapper = new DynamoDBMapper(client);
			
			DynamoDBQueryExpression<TrailPointRecord> queryExpression;
			Map<String, AttributeValue> queryAttributes;
			Map<String, String> dynamoDBReservedExpression = new HashMap<>();
			dynamoDBReservedExpression.put("#time", "TimeStamp"); //TimeStamp is a reserved keywords so use an alias
			
			Iterator<Integer> tileIterator = tileCoordinates.iterator();
			Integer tileCoord;
			List<TrailPointRecord> points;
			//iterate through and query for each coordinate tile requested
			//IMPORTENT: returned list of items has points for each tile grouped together; this should not changed
			//front-end expects this to be the case
			while(tileIterator.hasNext()) {
				tileCoord = tileIterator.next();
				queryAttributes = new HashMap<String, AttributeValue>();
				queryAttributes.put(":coord", new AttributeValue().withN(tileCoord.toString())); //coordinate tile to match
				queryAttributes.put(":startTime", new AttributeValue().withS(DataConverter.timeStamp(startTime))); //start time to filter by
				
				queryExpression = new DynamoDBQueryExpression<TrailPointRecord>()
						.withIndexName("Coordinate-TimeStamp-index") //query from index based on linear (1-dimensional) Coordinate
						.withConsistentRead(false)
						.withKeyConditionExpression("Coordinate = :coord and #time >= :startTime")
						.withExpressionAttributeValues(queryAttributes)
						.withExpressionAttributeNames(dynamoDBReservedExpression);
				
				points = mapper.query(TrailPointRecord.class, queryExpression);
				tileItems.put(tileCoord, points);
				totalPoints += points.size();
				
				//terminate loop and return error if allowed execution time is running out
				if(context.getRemainingTimeInMillis() <= 4000) { //4000 milliseconds or less remaining
					return new DatabaseTaskResult<Map<Integer, List<TrailPointRecord>>>(false, "Query Timeout", tileItems);
				}
			}	
		}
		catch(Exception e) { //error encountered when interfacing with AWS DynamoDB service
			return new DatabaseTaskResult<Map<Integer, List<TrailPointRecord>>>(false, "Database exception: " + e.getMessage(), null);
		}
		
		return new DatabaseTaskResult<Map<Integer, List<TrailPointRecord>>>(true, totalPoints + " trail records retrieved", tileItems);
	}

}
