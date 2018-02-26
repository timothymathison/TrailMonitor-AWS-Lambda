package com.umn.seniordesign.trailmonitor.lambda;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.umn.seniordesign.trailmonitor.entities.GetDataRequest;

public class ProcessGeoJsonRequest implements RequestHandler<GetDataRequest, String> {

    @Override
    public String handleRequest(GetDataRequest request, Context context) {
    	Map<String, String> params = request.getParams();
        context.getLogger().log("Request from <ip> with params: ");

//        return "Hello from Lambda! Here is what I received:" + request.getParams().toString();
        return "Hello from Lambda! Here are the params I received from " + request.getSourceIp() +": " + params;
    }

}
