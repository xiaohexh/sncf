package com.xh.sncf.netty.handler;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.xh.sncf.app.Executor;
import com.xh.sncf.app.NetSession;
import com.xh.sncf.logger.Logger;
import com.xh.sncf.net.Session;
import com.xh.sncf.request.RequestEvent;
import com.xh.sncf.request.RequestEventFactory;
import com.xh.sncf.request.RequestHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class DisruptorAdapterHandler<String> extends SimpleChannelInboundHandler<String> {
	
	public static final ConcurrentHashMap<ChannelId, Session> SESSIONS =
										new ConcurrentHashMap<ChannelId, Session>();
	
	private static final int DEFAULT_RING_BUFFER_SIZE= 8 * 1024;
	private static final ExecutorService CACHED_THREAD_POOL = Executors.newCachedThreadPool();
	private static final ThreadLocal<Disruptor<RequestEvent>> THREAD_LOCAL = new ThreadLocal<Disruptor<RequestEvent>>() {
		@SuppressWarnings("unchecked")
		@Override
		protected Disruptor<RequestEvent> initialValue() {
			Disruptor<RequestEvent> disruptor = new Disruptor<RequestEvent>(
					RequestEventFactory.DEFAULT, DEFAULT_RING_BUFFER_SIZE, CACHED_THREAD_POOL, ProducerType.SINGLE, new BlockingWaitStrategy());
			disruptor.handleEventsWith(new RequestHandler());
			disruptor.start();
			return disruptor;
		}
	};
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		
//		Logger.error("receive new connection request from client " + ctx.channel().remoteAddress() + ", channel id:" + ctx.channel().id());
		
		NetSession session = new NetSession(ctx.channel());
		SESSIONS.put(ctx.channel().id(), session);
		super.channelActive(ctx);
	}
	
	@Override
//	protected void channelRead0(ChannelHandlerContext ctx, O msg) throws Exception {
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		
//		Logger.error("recv msg from client:" + msg + ", channel id:" + ctx.channel().id());
		
		Session session = SESSIONS.get(ctx.channel().id());
		if (session == null) {
			return;
		}
		RingBuffer<RequestEvent> ringBuffer = THREAD_LOCAL.get().getRingBuffer();
		long next = ringBuffer.next();
		try {
			RequestEvent requestEvent = ringBuffer.get(next);
			requestEvent.setValues(newExecutor(session, msg));
		} finally {
			ringBuffer.publish(next);
//			Logger.error("put request to ring buffer");
		}
	}
	
//	protected abstract Executor newExecutor(Session session, O msg);
	protected abstract Executor newExecutor(Session session, String msg);

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		
		SocketAddress remoteAddr = ctx.channel().remoteAddress();
		
//		Logger.error("client " + remoteAddr + " close connection");
		
		Session session = SESSIONS.get(ctx.channel().id());
		if (session != null) {
			session.release();
		}
		super.channelInactive(ctx);
	}
	
}
