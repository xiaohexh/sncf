package com.xh.sncf.logger;

public final class Logger {
	
	private Logger() {
		
	}
	
	private static final org.slf4j.Logger Log = org.slf4j.LoggerFactory.getLogger("SNCF");
	
	public static void debug(String message) {
		Log.debug(message);
	}

	public static void debug(String message, Throwable throwable) {
		Log.debug(message, throwable);
	}

	public static void info(String message) {
		Log.info(message);
	}
	
	public static void info(String message, Throwable throwable) {
		Log.info(message, throwable);
	}
	
	public static void error(String message) {
		Log.error(message);
	}
	
	public static void error(String message, Throwable throwable) {
		Log.error(message, throwable);
	}
}
