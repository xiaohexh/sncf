package com.xh.sncf.request;

import com.xh.sncf.app.Executor;

public class RequestEvent {

	private Executor executor;
	
	public Executor getExecutor() {
		return executor;
	}
	
	public void setValues(Executor executor) {
		this.executor= executor;
	}
	
	public void clearValues() {
		setValues(null);
	}
}
