package com.xh.sncf.example;

import org.apache.log4j.PropertyConfigurator;

import com.xh.sncf.logger.Logger;

public class SncfTest {

	public static void main(String[] args) {
		
    	// initial log
    	String logPath = System.getProperty("configPath") + "/log4j.properties";
    	PropertyConfigurator.configureAndWatch(logPath, 5 * 60000);
		
    	Logger.error("log config path:" + logPath);
    	
    	
		int port = 18080;
		TcpServer server = new TcpServer(port);
		
		server.start();
	}
	
}
