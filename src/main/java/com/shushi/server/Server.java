package com.shushi.server;

import com.shushi.handler.ShushiServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author feiyang.d
 * @date 2018/7/30
 */

public class Server {

    private int port;//端口
    public Server(int port){
        this.port=port;
    }

    @Autowired
    private ShushiServerHandler echoServerHandler;

    public void run() throws Exception {
        // 调度线程，一般和cpu核心数相当
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) {
                    socketChannel.pipeline()
                            /*.addLast(new LineBasedFrameDecoder(1024))
                            .addLast(new StringDecoder())*/
                            // 业务处理的handler
                            .addLast(echoServerHandler);
                }
                /*
                   第一个option设置是如果处理线程(workerGroup)全满，则将请求放入队列，队列最大长度为128
                   第二个childOption设置 是否启用心跳保活机制。在双方TCP套接字建立连接后（即都进入ESTABLISHED状态）并且在两个小时左右上层没有任何数据传输的情况下，这套机制才会被激活。
                 */
            }).option(ChannelOption.SO_BACKLOG,128).childOption(ChannelOption.SO_KEEPALIVE,true);
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            /*
              优雅关闭线程
             */
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        context.start();
    }
}
