package com.xh.sncf.app;

import java.util.concurrent.TimeUnit;

import com.xh.sncf.logger.Logger;
import com.xh.sncf.net.Connector;
import com.xh.sncf.net.Session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

public class NetSession implements Session {
	
	private volatile Channel channel;
	private volatile Connector connector;
	
	public NetSession(Channel channel) {
		this.channel = channel;
	}

	@Override
	public Channel channel() {
		return channel;
	}

	@Override
	public boolean isOnline() {
		return channel != null && channel.isActive();
	}

	@Override
	public Connector getConnector() {
		return connector;
	}

	@Override
	public void setConnector(Connector connector) {
		this.connector = connector;
	}

	@Override
	public void writeAndFlush(Object msg) {
		writeAndFlush(msg, null);
	}

	@Override
	public void writeAndFlush(Object message, ChannelFutureListener listener) {
		String msg = (String)message;
//		Logger.error("NetSession writeAndFlush, msg:" + msg);
		if (channel == null || !channel.isActive()) {
			Logger.error("NetSession writeAndFlush channel is null || is not active");
			return;
		}
		
		if (channel.isWritable()) {
			if (listener == null) {
//				Logger.error("NetSession writeAndFlush channel is writable, and listener is null, channel id:" + channel.id() + ", msg:" + msg);
				try {
					channel.writeAndFlush(msg).sync();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				Logger.error("NetSession writeAndFlush channel is writable, and listener is not null");
				channel.write(msg).addListener(listener);
			}
		} else {
			Logger.error("NetSession writeAndFlush channel is not writable");
			channel.eventLoop().schedule(() -> {
				writeAndFlush(msg, listener);
			}, 1L, TimeUnit.SECONDS);
		}
	}

	@Override
	public void close() {
		if (channel != null && channel.isActive()) {
			channel.close();
			channel = null;
		}
	}
	
	@Override
	public void release() {
		channel = null;
		if (null != connector) {
			connector.disconnect();
			connector.setSession(null);
			connector = null;
		}
	}
	
}
