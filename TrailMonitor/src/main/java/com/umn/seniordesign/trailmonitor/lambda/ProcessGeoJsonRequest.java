package com.umn.seniordesign.trailmonitor.lambda;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.umn.seniordesign.trailmonitor.entities.GetDataRequest;
import com.umn.seniordesign.trailmonitor.entities.TrailPointRecord;
import com.umn.seniordesign.trailmonitor.services.DatabaseTask;
import com.umn.seniordesign.trailmonitor.services.DatabaseTaskResult;

public class ProcessGeoJsonRequest implements RequestHandler<GetDataRequest, String> {

    @Override
    public String handleRequest(GetDataRequest request, Context context) {
//    	Map<String, String> params = request.getParams();
//        context.getLogger().log("Request from IP: " + request.getSourceIp() + " with params: " + params.toString());
        
        //TODO: Authenticate data request
        
        //hard coded time for testing
        Calendar startTime = new Calendar.Builder().build();
        startTime.setTimeInMillis(0);
        
        DatabaseTaskResult<List<TrailPointRecord>> result = DatabaseTask.readItems(
        		Arrays.asList(17954, 17909), startTime); //hard coded tiles for testing
        if(!result.isSuccess()) {
        	context.getLogger().log("Internal Server Error: " + result.getMessage()); //logged in cloud watch
        	return result.getMessage();//new PostDataResponse(500, "Error saving data");
        }
        
        //TODO: convert trail records to GeoJson data
        
        List<TrailPointRecord> items = result.getData();
    	return items.toString();

//        return "Hello from Lambda! Here are the params I received from " + request.getSourceIp() +": " + params;
    }

}
