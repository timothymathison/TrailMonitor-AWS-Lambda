package com.umn.seniordesign.trailmonitor.lambda;

import java.util.Iterator;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.umn.seniordesign.trailmonitor.entities.PostDataRequest;
import com.umn.seniordesign.trailmonitor.entities.PostDataResponse;
import com.umn.seniordesign.trailmonitor.entities.TrailPoint;
import com.umn.seniordesign.trailmonitor.entities.TrailPointRecord;
import com.umn.seniordesign.trailmonitor.services.DatabaseTask;
import com.umn.seniordesign.trailmonitor.services.DatabaseTaskResult;
import com.umn.seniordesign.trailmonitor.utilities.DataTypeMapper;

public class ProcessDataFromPost implements RequestHandler<PostDataRequest, PostDataResponse> {

	/**
	 * <h1>Processes Trail Data from Post request</h1>
	 * @param: request - PostDataRequest object parsed from Json in API
	 * @param: context - extra information about request
	 * @return PostDataResponse object
	 */
    public PostDataResponse handleRequest(PostDataRequest request, Context context) {
    	List<TrailPoint> data = request.getData();
        context.getLogger().log(data.size() + " trail data points recieved"); //logged in cloud watch
        
        if(request.getDeviceId() == null) {
        	return new PostDataResponse(400, "Request missing deviceId");
        }
        
        List<TrailPointRecord> records;
        try {
        	records = DataTypeMapper.makeRecords(data, request.getDeviceId()); //convert data to database format
        }
        catch(Exception e) {
        	return new PostDataResponse(400, e.getMessage());
        }
        
        DatabaseTaskResult<Object> result = DatabaseTask.saveItems(records);  //save to database
        if(!result.isSuccess()) {
        	return new PostDataResponse(500, result.getMessage());
        }
        
        //everything worked!
        PostDataResponse response = new PostDataResponse(200, "Hello from Lambda! " + data.size() 
        		+ " trail points were received");
        response.setEcho(Output(request.getData()));
        return response;
    }
    
    /**
     * Utility that converts input data to string (used to echo data)
     * @param input - input data
     * @return String representation of data
     */
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
