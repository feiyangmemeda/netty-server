package com.shushi.handler;

import com.shushi.action.UserAction;
import com.shushi.channel.ChannelService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;

/**
 * @author feiyang.d
 * @date 2018/7/30
 */
@Component
@ChannelHandler.Sharable
public class ShushiServerHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private UserAction userAction;

    @Autowired
    private ChannelService channelService;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        /*ctx.write(Unpooled.copiedBuffer("dfy very cool".getBytes("UTF-8")));
        ctx.write(Unpooled.copiedBuffer("1".getBytes("UTF-8")));
        ctx.write(Unpooled.copiedBuffer(new byte[]{1, 1, 1}));*/

        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();

        channelService.addChannel(clientIP+UUID.randomUUID(),ctx.channel());
        ctx.channel().read();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);


        System.out.println("Client say:" + new String(bytes)+",and bytes length is "+bytes.length);



        //ByteBuf writebuf = Unpooled.copiedBuffer(new byte[]{1});
        //ctx.writeAndFlush(writebuf);//发送到客户端

        /*for(Map.Entry<String,Channel> entry : channelService.channelMap.entrySet()){
            entry.getValue().writeAndFlush(Unpooled.copiedBuffer(new byte[]{1}));
        }*/
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }




}
