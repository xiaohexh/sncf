package com.xh.sncf.request;


import com.lmax.disruptor.EventHandler;
import com.xh.sncf.app.Executor;
import com.xh.sncf.logger.Logger;

public class RequestHandler implements EventHandler<RequestEvent> {

	@Override
	public void onEvent(RequestEvent event, long sequence, boolean endofBatch) throws Exception {
		try {
			Executor executor = event.getExecutor();
//			Logger.error("get new request from ring buffer");
			if (null != executor) {
				try {
					executor.onExecute();
				} finally {
					executor.release();
				}
			}
		} finally {
			event.clearValues();
		}
	}

}
