package com.umn.seniordesign.trailmonitor.lambda;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class ProcessGeoJsonRequest implements RequestHandler<Map<String, String>, String> {

    @Override
    public String handleRequest(Map<String, String> params, Context context) {
        context.getLogger().log("Request from <ip> with params: " + params);

        // TODO: implement your handler
        return "Hello from Lambda!";
    }

}
