package com.xh.sncf.server;

import com.xh.sncf.logger.Logger;
import com.xh.sncf.netty.NettyBootstrap;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public abstract class TcpProtocolServer implements NettyBootstrap<ServerBootstrap> {
	
	private ServerBootstrap bootstrap = null;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	protected int port;
	
	public TcpProtocolServer(int port) {
		this.port = port;
	}
	
	/**
	 * Is netty transport native epoll
	 */
	protected static boolean isEpollAvailable = false;
	
	static {
		isEpollAvailable = Epoll.isAvailable();
	}

	@Override
	public ServerBootstrap createBootstrap() {
		
		Logger.error("create Bootstrap");

		bootstrap = new ServerBootstrap();
		if (isEpollAvailable) {
			Logger.error("use EpollEventLoopGroup");
			this.bossGroup = new EpollEventLoopGroup();
			this.workerGroup = new EpollEventLoopGroup();
			bootstrap.channel(EpollServerSocketChannel.class);
		} else {
			Logger.error("use NioEventLoopGroup");
			this.bossGroup = new NioEventLoopGroup();
			this.workerGroup = new NioEventLoopGroup();
			bootstrap.channel(NioServerSocketChannel.class);
		}

		bootstrap.group(bossGroup, workerGroup);
		bootstrap.childHandler(newChannelInitializer());
		bootstrap.option(ChannelOption.SO_REUSEADDR, true);
//		bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
//		bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		
		return bootstrap;
	}
	
	protected abstract ChannelHandler newChannelInitializer();

	@Override
	public void start() {
		
		Logger.error("starting server");
		
		ServerBootstrap sb = bootstrap() != null ? bootstrap() : createBootstrap();
		try {
			ChannelFuture future = sb.bind(port()).sync();
		} catch (InterruptedException e) {
			Logger.error("server start failed:" + e.getMessage());
			e.printStackTrace();
			stop();
		} finally {
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

				@Override
				public void run() {
					stop();
				}
				
			}));
		}
		
		Logger.error("server start successfully!");
	}

	@Override
	public void stop() {
		
		Logger.info("server is stopping");
		
		if (bossGroup != null) {
			bossGroup.shutdownGracefully();
			bossGroup = null;
		}
		
		if (workerGroup != null) {
			workerGroup.shutdownGracefully();
			workerGroup = null;
		}
		
		bootstrap = null;
		port = 0;
	}

	@Override
	public ServerBootstrap bootstrap() {
		return bootstrap;
	}
	
	public EventLoopGroup bossGroup() {
		return bossGroup;
	}
	
	public EventLoopGroup workerGroup() {
		return workerGroup;
	}
	
	public int port() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
}
