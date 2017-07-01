package com.xh.sncf.request;

import com.lmax.disruptor.EventFactory;

public class RequestEventFactory implements EventFactory<RequestEvent> {

	@Override
	public RequestEvent newInstance() {
		return new RequestEvent();
	}

	public static final RequestEventFactory DEFAULT = new RequestEventFactory();
	
}
