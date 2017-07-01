package com.xh.sncf.example;

import com.xh.sncf.app.Executor;
import com.xh.sncf.net.Session;
import com.xh.sncf.netty.handler.DisruptorAdapterHandler;

//public class MySocketHandler extends DisruptorAdapterHandler<Object> {
public class MySocketHandler extends DisruptorAdapterHandler<String> {
	
	@Override
//	protected Executor newExecutor(Session session, Object msg) {
	protected Executor newExecutor(Session session, String msg) {
		return new MyExecutor(session, msg);
	}

}
