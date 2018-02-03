package com.umn.seniordesign.trailmonitor.lambda;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class ProcessDataFromPost implements RequestHandler<List<Map<String,String>>, String> {

    public String handleRequest(List<Map<String, String>> data, Context context) {
        context.getLogger().log("Data recieved");

        // TODO: implement your handler
        return "Hello from Lambda! Here is the data I recieved:" + Output(data);
    }
    
    static String Output(List<Map<String, String>> input) {
    	StringBuilder str = new StringBuilder();
    	str.append("[");
    	Iterator<Map<String, String>> iterator = input.iterator();
    	while(iterator.hasNext()) {
    		str.append(iterator.next());
//    		str.append("\n");
    	}
    	str.append("]");
    	return str.toString();
    }

}
