package com.xh.sncf.netty;

public interface NettyBootstrap<T> {

	T createBootstrap();
	
	void start();
	
	void stop();
	
	T bootstrap();
}
