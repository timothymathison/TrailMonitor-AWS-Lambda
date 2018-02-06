package com.umn.seniordesign.trailmonitor.lambda;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.umn.seniordesign.trailmonitor.entities.PostDataRequest;
import com.umn.seniordesign.trailmonitor.entities.PostDataResponse;
import com.umn.seniordesign.trailmonitor.entities.TrailPoint;

public class ProcessDataFromPost implements RequestHandler<PostDataRequest, PostDataResponse> {

    public PostDataResponse handleRequest(PostDataRequest request, Context context) {
        context.getLogger().log("Data recieved");

        // TODO: implement your handler
        PostDataResponse response = new PostDataResponse(200, "Hello from Lambda! " + request.getData().size() 
        		+ " trail points were received");
        response.setEcho(Output(request.getData()));
        return response;
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
