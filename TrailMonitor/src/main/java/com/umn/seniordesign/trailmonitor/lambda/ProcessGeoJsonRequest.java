package com.umn.seniordesign.trailmonitor.lambda;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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
        	return new GetDataResponse<GeoJson>(500, "Error Retrieving GeoJson data", new GeoJson(GeoJson.Types.FeatureCollection));
        }
        
        //convert trail records to GeoJson data
        GeoJson geoJson;
        try {
        	geoJson = DataConverter.buildGeoJson(result.getData(), context.getLogger());
        }
        catch(Exception e) { //error encountered building GeoJson data
        	context.getLogger().log("Internal Server Error: " + e.getMessage()); //logged in cloud watch
        	return new GetDataResponse<GeoJson>(500, "Error Retrieving GeoJson data", new GeoJson(GeoJson.Types.FeatureCollection));
        }
        
//        context.getLogger().log("# of features in GeoJson: " + geoJson.getFeatures().size());
        
//        List<TrailPointRecord> items = result.getData();
        context.getLogger().log("Success: " + result.getMessage());
    	return new GetDataResponse<GeoJson>(200, result.getMessage(), geoJson);

//        return "Hello from Lambda! Here are the params I received from " + request.getSourceIp() +": " + params;
    }

}
