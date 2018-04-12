package com.umn.seniordesign.trailmonitor.lambda;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.umn.seniordesign.trailmonitor.entities.GeoTrailInfo;
import com.umn.seniordesign.trailmonitor.entities.GetDataRequest;
import com.umn.seniordesign.trailmonitor.entities.GetDataResponse;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class ProcessGeoJsonRequestTest {

    private static GetDataRequest input;

    @BeforeClass
    public static void createInput() throws IOException {
        //set up your sample input object here.
        input = new GetDataRequest();
        Map<String, String> params = new HashMap<String, String>();
        params.put("lim-top", "45");
        params.put("lim-left", "-95");
        params.put("lim-bot", "44");
        params.put("lim-right", "-93");
        params.put("start-time", "0");
        params.put("zoom", "5");
        input.setParams(params);
    }

    private Context createContext() {
        TestContext ctx = new TestContext();

        //customize your context here if needed.
        ctx.setFunctionName("Your Function Name");

        return ctx;
    }

    @Test
    public void testProcessGeoJsonRequest() {
        ProcessGeoJsonRequest handler = new ProcessGeoJsonRequest();
        Context ctx = createContext();

        GetDataResponse<GeoTrailInfo> output = handler.handleRequest(input, ctx);

        //validate output here if needed.
        Assert.assertEquals("Hello from Lambda!", output);
    }
}
