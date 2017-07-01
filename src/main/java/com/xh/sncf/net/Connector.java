package com.xh.sncf.net;

public interface Connector {

	boolean isConnected();
	
	Session session();
	
	void setSession(Session session);
	
	void disconnect();
}
