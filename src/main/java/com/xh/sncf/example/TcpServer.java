package com.xh.sncf.example;

import com.xh.sncf.server.TcpProtocolServer;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class TcpServer extends TcpProtocolServer {
	
	private int port;
	
	public TcpServer(int port) {
		super(port);
		this.port = port;
	}

	@Override
	protected ChannelHandler newChannelInitializer() {
		
		return new ChannelInitializer<EpollSocketChannel>() {

			@Override
			protected void initChannel(EpollSocketChannel channel) throws Exception {
				channel.pipeline().addLast("split",new DelimiterBasedFrameDecoder(1000, Delimiters.lineDelimiter()));
                channel.pipeline().addLast("decoder",new StringDecoder());//对字符串进行处理  解码器
                channel.pipeline().addLast("encoder",new StringEncoder());//对字符串进行处理  编码器
                channel.pipeline().addLast("handler",new MySocketHandler());
			}
			
		};
	}
	
	@Override
	public void stop() {
		super.stop();
	}

}
