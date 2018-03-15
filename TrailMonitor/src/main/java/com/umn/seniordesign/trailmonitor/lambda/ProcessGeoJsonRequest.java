package com.umn.seniordesign.trailmonitor.lambda;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.umn.seniordesign.trailmonitor.entities.GetDataRequest;
import com.umn.seniordesign.trailmonitor.entities.GetDataResponse;
import com.umn.seniordesign.trailmonitor.entities.TrailPointRecord;
import com.umn.seniordesign.trailmonitor.entities.geojson.GeoJson;
import com.umn.seniordesign.trailmonitor.services.DatabaseTask;
import com.umn.seniordesign.trailmonitor.services.DatabaseTaskResult;
import com.umn.seniordesign.trailmonitor.utilities.DataConverter;

public class ProcessGeoJsonRequest implements RequestHandler<GetDataRequest, GetDataResponse<GeoJson>> {

    @Override
    public GetDataResponse<GeoJson> handleRequest(GetDataRequest request, Context context) {
    	Map<String, String> params = request.getParams();
        context.getLogger().log("Request from IP: " + request.getSourceIp() + " with params: " 
        		+ (params != null ? params.toString() : "none")); //logged to cloud watch
        
        //TODO: Authenticate data request
        
        //extract start-time from query parameters
        Calendar startTime = new Calendar.Builder().build();
        try {
        	startTime.setTimeInMillis(Long.parseLong(params.get("start-time")));
        }
        catch(NullPointerException | NumberFormatException e) {
        	context.getLogger().log("Bad Request: missing or invalid start-time parameter");
        	return new GetDataResponse<GeoJson>(400, "Missing or invalid start-time parameter", null);
        }
        
        //translate the rectangular GPS limits into a list of tile coordinates
        List<Integer> tiles = DataConverter.parseRequestCoords(params);
        if(tiles.size() == 0) {
        	context.getLogger().log("Bad Request: missing or invalid GPS limit parameters");
        	return new GetDataResponse<GeoJson>(400, "Missing or invalid GPS limit parameters", null);
        }
        
        //query database
        DatabaseTaskResult<List<TrailPointRecord>> result = DatabaseTask.readItems(tiles, startTime, context);
        if(!result.isSuccess()) {
        	if(!result.getMessage().equals("Query Timeout")) {
        		context.getLogger().log("Internal Server Error: " + result.getMessage()); //logged in cloud watch
            	return new GetDataResponse<GeoJson>(500, "Error Retrieving GeoJson data", null);
            	//TODO: figure out why returning "new GeoJson(GeoJson.Types.FeatureCollection)" messes up return object api mapping
        	}
        	else {
        		context.getLogger().log("Query Timeout: Not enough time to query for all requested tiles"); //logged in cloud watch
            	return new GetDataResponse<GeoJson>(400, "Error Retrieving GeoJson data: Query Timeout (Not enough time to query for all requested tiles)",
            			null);
        	}
        	
        }
        
        //convert trail records to GeoJson data
        GeoJson geoJson;
        try {
        	geoJson = DataConverter.buildGeoJson(result.getData());
        }
        catch(Exception e) { //error encountered building GeoJson data
        	context.getLogger().log("Internal Server Error: " + e.getMessage()); //logged in cloud watch
        	return new GetDataResponse<GeoJson>(500, "Error Retrieving GeoJson data", new GeoJson(GeoJson.Types.FeatureCollection));
        }
        
        //Everything worked!
        context.getLogger().log("Success: " + result.getMessage());
    	return new GetDataResponse<GeoJson>(200, result.getMessage(), geoJson);
    }
}
