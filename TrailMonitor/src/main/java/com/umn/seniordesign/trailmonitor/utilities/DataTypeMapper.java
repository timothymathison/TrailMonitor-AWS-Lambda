package com.umn.seniordesign.trailmonitor.utilities;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.umn.seniordesign.trailmonitor.entities.TrailPoint;
import com.umn.seniordesign.trailmonitor.entities.TrailPointRecord;

public class DataTypeMapper {

	public static TrailPointRecord makeRecord(TrailPoint data, long deviceId) {
		
		//TODO: check data integrity/completeness
		
		TrailPointRecord record = new TrailPointRecord();
		record.setAllCoordinates(data.getLatitude(), data.getLongitude());
		record.setTimeStampFromMilli(data.getTimeStamp());
		record.setDeviceId(deviceId);
		record.setValue(data.getValue());
		return record;
	}
	
	public static List<TrailPointRecord> makeRecords(List<TrailPoint> data, long deviceId) {
		List<TrailPointRecord> records = new LinkedList<TrailPointRecord>();
		Iterator<TrailPoint> iterator = data.iterator();
		
		while(iterator.hasNext()) {
			records.add(makeRecord(iterator.next(), deviceId));
		}
		
		return records;
	}
}
