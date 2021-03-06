package com.umn.seniordesign.trailmonitor.lambda;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.umn.seniordesign.trailmonitor.entities.PostDataRequest;
import com.umn.seniordesign.trailmonitor.entities.PostDataResponse;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class ProcessDataFromPostTest {

    private static PostDataRequest input;

    @BeforeClass
    public static void createInput() throws IOException {
        //set up your sample input object here.
        input = null;
    }

    private Context createContext() {
        TestContext ctx = new TestContext();

        //customize your context here if needed.
        ctx.setFunctionName("Your Function Name");

        return ctx;
    }

    @Test
    public void testProcessDataFromPost() {
        ProcessDataFromPost handler = new ProcessDataFromPost();
        Context ctx = createContext();

        PostDataResponse output = handler.handleRequest(input, ctx);

        //validate output here if needed.
        Assert.assertEquals("Hello from Lambda!", output);
    }
}
