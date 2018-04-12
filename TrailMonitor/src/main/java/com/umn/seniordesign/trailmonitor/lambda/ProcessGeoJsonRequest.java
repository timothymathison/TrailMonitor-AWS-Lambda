package com.umn.seniordesign.trailmonitor.lambda;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.umn.seniordesign.trailmonitor.entities.GeoTrailInfo;
import com.umn.seniordesign.trailmonitor.entities.GetDataRequest;
import com.umn.seniordesign.trailmonitor.entities.GetDataResponse;
import com.umn.seniordesign.trailmonitor.entities.TrailPointRecord;
import com.umn.seniordesign.trailmonitor.services.DatabaseTask;
import com.umn.seniordesign.trailmonitor.services.DatabaseTaskResult;
import com.umn.seniordesign.trailmonitor.utilities.DataConverter;
import com.umn.seniordesign.trailmonitor.utilities.GeoJsonBuilder;

public class ProcessGeoJsonRequest implements RequestHandler<GetDataRequest, GetDataResponse<GeoTrailInfo>> {

    @Override
    public GetDataResponse<GeoTrailInfo> handleRequest(GetDataRequest request, Context context) {
    	Map<String, String> params = request.getParams();
    	LambdaLogger logger = context.getLogger();
        logger.log("Request from IP: " + request.getSourceIp() + " with params: " 
        		+ (params != null ? params.toString() : "none")); //logged to cloud watch
        
        //TODO: Authenticate data request
        
        //extract start-time from query parameters
        Calendar startTime = new Calendar.Builder().build();
        try {
        	startTime.setTimeInMillis(Long.parseLong(params.get("start-time")));
        }
        catch(NullPointerException | NumberFormatException e) {
        	logger.log("Bad Request: missing or invalid start-time parameter");
        	return new GetDataResponse<GeoTrailInfo>(400, "Missing or invalid start-time parameter", null);
        }
        
        //extract zoom from query parameters
        Integer zoomDepth;
        String zoom = params.get("zoom");
        if(zoom == null) { //zoom parameter not provided, proceed but will not execute zoom dependent processing on data
        	zoomDepth = -1; //TODO: decide whether requests without a zoom should be rejected
        }
        else {
        	try {
            	zoomDepth = GeoTrailInfo.getZoomDepth(Integer.parseInt(zoom));
            }
            catch(NumberFormatException e) {
            	logger.log("Bad Request: invalid zoom parameter");
            	return new GetDataResponse<GeoTrailInfo>(400, "Invalid zoom parameter", null);
            }
        	if(zoomDepth == null) {
            	logger.log("Bad Request: unsupported zoom");
            	//                                            send non null GeoTrailInfo data so zoom range options are included
            	return new GetDataResponse<GeoTrailInfo>(400, "Unsupported zoom", new GeoTrailInfo());
            }
        }
        
        //translate the rectangular GPS limits into a list of tile coordinates
        List<Integer> tiles = DataConverter.parseRequestCoords(params);
        if(tiles.size() == 0) {
        	logger.log("Bad Request: missing or invalid GPS limit parameters");
        	return new GetDataResponse<GeoTrailInfo>(400, "Missing or invalid GPS limit parameters", null);
        }
        //check # of requested tiles against maximum (somewhat arbitrary) that should be queried in one request
        else if(tiles.size() > 2000) {
        	logger.log("Bad Request: too many tiles requested");
        	return new GetDataResponse<GeoTrailInfo>(400, "Data request for too large of an area, try requesting fewer tiles at a time", null);
        }
        
        //query database
        DatabaseTaskResult<Map<Integer, List<TrailPointRecord>>> result = DatabaseTask.readPoints(tiles, startTime, context);
        if(!result.isSuccess()) {
        	if(!result.getMessage().equals("Query Timeout")) {
        		logger.log("Internal Server Error: " + result.getMessage()); //logged in cloud watch
            	return new GetDataResponse<GeoTrailInfo>(500, "Error Retrieving GeoJson data", null);
        	}
        	else {
        		logger.log("Query Timeout: Not enough time to query for all requested tiles"); //logged in cloud watch
            	return new GetDataResponse<GeoTrailInfo>(400, "Error Retrieving GeoJson data: Query Timeout (Not enough time to query for all requested tiles)",
            			null);
        	}	
        }
        
        //convert trail records to GeoJson data
        GeoTrailInfo geoJson;
        try {
        	geoJson = GeoJsonBuilder.build(result.getData(), zoomDepth);
        }
        catch(Exception e) { //error encountered building GeoJson data
        	logger.log("Internal Server Error: " + e.getMessage()); //logged in cloud watch
        	return new GetDataResponse<GeoTrailInfo>(500, "Error Retrieving GeoJson data", null);
        }
        
        //Everything worked!
        String successMsg = "Success: " + geoJson.getTiles().size() + " Tile(s) retrieved";
        logger.log(successMsg);
    	return new GetDataResponse<GeoTrailInfo>(200, successMsg, geoJson);
    }
}
