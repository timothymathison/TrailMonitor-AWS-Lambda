package com.umn.seniordesign.trailmonitor.utilities;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.umn.seniordesign.trailmonitor.entities.TrailPoint;
import com.umn.seniordesign.trailmonitor.entities.TrailPointRecord;

public class DataTypeMapper {

	/**
	 * <h1>Converts TrailPoint class to TrailPointRecord (database class)</h1>
	 * @param data - TrailPoint class object
	 * @param deviceId - deviceId associated with data
	 * @return TrailPointRecord class object
	 * @throws Exception Invalid data
	 */
	public static TrailPointRecord makeRecord(TrailPoint data, long deviceId) throws Exception {
		if(!data.valid()) { //check validity and completeness of data
			throw new Exception("Missing or invalid data");
		}
		
		TrailPointRecord record = new TrailPointRecord();
		record.setAllCoordinates(data.getLatitude(), data.getLongitude());
		record.setTimeStampFromMilli(data.getTimeStamp());
		record.setDeviceId(deviceId);
		record.setValue(data.getValue());
		return record;
	}
	
	/**
	 * <h1>Converts list of TrailPoint class objects to TrailPointRecord (database class) objects</h1>
	 * Calls {@link #makeRecord(TrailPoint, long)} for each list element
	 * @param data - List of TrailPoint class objects
	 * @param deviceId - deviceId associated with data
	 * @return List of TrailPointRecord objects
	 * @throws Exception Invalid data
	 */
	public static List<TrailPointRecord> makeRecords(List<TrailPoint> data, long deviceId) throws Exception {
		List<TrailPointRecord> records = new LinkedList<TrailPointRecord>();
		Iterator<TrailPoint> iterator = data.iterator();
		
		while(iterator.hasNext()) {
			records.add(makeRecord(iterator.next(), deviceId));
		}
		
		return records;
	}
}
