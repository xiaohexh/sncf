package com.xh.sncf.example;

import com.xh.sncf.app.Executor;
import com.xh.sncf.logger.Logger;
import com.xh.sncf.net.Session;

public class MyExecutor implements Executor {
	
	protected Session session;
	protected String request;
	
	public MyExecutor(Session session, String request) {
		this.session = session;
		this.request = request;
	}

	@Override
	public void release() {
		this.session = null;
		this.request = null;
	}

	@Override
	public void onExecute() {
		if (request == null) {
			return;
		}
//		Logger.error("handle new request, write msg back:" + request);
		session.writeAndFlush(request);
	}

}
