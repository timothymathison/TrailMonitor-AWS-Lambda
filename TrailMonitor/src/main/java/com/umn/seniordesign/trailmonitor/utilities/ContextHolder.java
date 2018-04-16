package com.umn.seniordesign.trailmonitor.utilities;

import com.amazonaws.services.lambda.runtime.Context;

public class ContextHolder {
	private static Context context;
	
	public static void setContext(Context fromContext) {
		context = fromContext;
	}
	
	public static Context getContext() {
		return context;
	}
}
