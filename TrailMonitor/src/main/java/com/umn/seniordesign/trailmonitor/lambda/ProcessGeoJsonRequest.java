package com.umn.seniordesign.trailmonitor.lambda;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.umn.seniordesign.trailmonitor.entities.GetDataRequest;
import com.umn.seniordesign.trailmonitor.entities.GetDataResponse;
import com.umn.seniordesign.trailmonitor.entities.TrailPointRecord;
import com.umn.seniordesign.trailmonitor.entities.geojson.GeoJsonTile;
import com.umn.seniordesign.trailmonitor.services.DatabaseTask;
import com.umn.seniordesign.trailmonitor.services.DatabaseTaskResult;
import com.umn.seniordesign.trailmonitor.utilities.DataConverter;
import com.umn.seniordesign.trailmonitor.utilities.GeoJsonBuilder;

public class ProcessGeoJsonRequest implements RequestHandler<GetDataRequest, GetDataResponse<GeoJsonTile>> {

    @Override
    public GetDataResponse<GeoJsonTile> handleRequest(GetDataRequest request, Context context) {
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
        	return new GetDataResponse<GeoJsonTile>(400, "Missing or invalid start-time parameter", null);
        }
        
        //translate the rectangular GPS limits into a list of tile coordinates
        List<Integer> tiles = DataConverter.parseRequestCoords(params);
        if(tiles.size() == 0) {
        	context.getLogger().log("Bad Request: missing or invalid GPS limit parameters");
        	return new GetDataResponse<GeoJsonTile>(400, "Missing or invalid GPS limit parameters", null);
        }
        //check # of requested tiles against maximum (somewhat arbitrary) that should be queried in one request
        else if(tiles.size() > 2000) {
        	context.getLogger().log("Bad Request: too many tiles requested");
        	return new GetDataResponse<GeoJsonTile>(400, "Data request for too large of an area, try requesting fewer tiles at a time", null);
        }
        
        //query database
        DatabaseTaskResult<Map<Integer, List<TrailPointRecord>>> result = DatabaseTask.readPoints(tiles, startTime, context);
        if(!result.isSuccess()) {
        	if(!result.getMessage().equals("Query Timeout")) {
        		context.getLogger().log("Internal Server Error: " + result.getMessage()); //logged in cloud watch
            	return new GetDataResponse<GeoJsonTile>(500, "Error Retrieving GeoJson data", null);
        	}
        	else {
        		context.getLogger().log("Query Timeout: Not enough time to query for all requested tiles"); //logged in cloud watch
            	return new GetDataResponse<GeoJsonTile>(400, "Error Retrieving GeoJson data: Query Timeout (Not enough time to query for all requested tiles)",
            			new GeoJsonTile(GeoJsonTile.Types.FeatureCollection));
        	}
        	
        }
        
        //convert trail records to GeoJson data
        GeoJsonTile geoJson;
        try {
        	geoJson = GeoJsonBuilder.build(result.getData());
        }
        catch(Exception e) { //error encountered building GeoJson data
        	context.getLogger().log("Internal Server Error: " + e.getMessage()); //logged in cloud watch
        	return new GetDataResponse<GeoJsonTile>(500, "Error Retrieving GeoJson data", new GeoJsonTile(GeoJsonTile.Types.FeatureCollection));
        }
        
        //Everything worked!
        context.getLogger().log("Success: " + result.getMessage());
    	return new GetDataResponse<GeoJsonTile>(200, result.getMessage(), geoJson);
    }
}
