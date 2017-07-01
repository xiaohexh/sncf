package com.xh.sncf.net;

import com.xh.sncf.app.Releasable;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

public interface Session extends Releasable, AutoCloseable {

	Channel channel();
	
	boolean isOnline();
	
	Connector getConnector();
	
	void setConnector(Connector connector);
	
	void writeAndFlush(Object msg);
	
	void writeAndFlush(Object msg, ChannelFutureListener listener);
	
	void close();
}
