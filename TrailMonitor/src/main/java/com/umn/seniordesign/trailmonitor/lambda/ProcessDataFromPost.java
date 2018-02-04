package com.umn.seniordesign.trailmonitor.lambda;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.umn.seniordesign.trailmonitor.entities.TrailPoint;

public class ProcessDataFromPost implements RequestHandler<List<TrailPoint>, String> {

    public String handleRequest(List<TrailPoint> data, Context context) {
        context.getLogger().log("Data recieved");

        // TODO: implement your handler
        return "Hello from Lambda! Here is the data I recieved:" + Output(data);
    }
    
    static String Output(List<TrailPoint> input) {
    	StringBuilder str = new StringBuilder();
    	str.append("[");
    	Iterator<TrailPoint> iterator = input.iterator();
    	while(iterator.hasNext()) {
    		str.append(iterator.next());
    		str.append(", ");
    	}
    	str.append("]");
    	return str.toString();
    }

}
