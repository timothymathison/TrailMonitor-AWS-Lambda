package com.umn.seniordesign.trailmonitor.lambda;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.umn.seniordesign.trailmonitor.entities.GetDataRequest;

public class ProcessGeoJsonRequest implements RequestHandler<GetDataRequest, String> {

    @Override
    public String handleRequest(GetDataRequest request, Context context) {
    	Map<String, String> params = request.getParams();
        context.getLogger().log("Request from IP: " + request.getSourceIp() + " with params: " + params.toString());
        
        //TODO: Authenticate data request
        
        //TODO: query database for trail data
        //TODO: convert trail records to GeoJson data

        return "Hello from Lambda! Here are the params I received from " + request.getSourceIp() +": " + params;
    }

}
