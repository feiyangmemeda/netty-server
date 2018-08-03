package com.shushi.channel;

import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author feiyang.d
 * @date 2018/8/1
 */
@Service
public class ChannelService {

    public ConcurrentHashMap<String,Channel> channelMap = new ConcurrentHashMap<String, Channel>();

    public void addChannel(String ip,Channel channel){
        channelMap.put(ip,channel);
    }

    public Channel getChannel(String ip){
        return channelMap.get(ip);
    }

}
