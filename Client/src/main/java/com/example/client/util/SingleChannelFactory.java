package com.example.client.util;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

@Component
public class SingleChannelFactory {
    private volatile Channel channel = null;

    private SingleChannelFactory(){

    }
    public void setChannel(Channel channel){
        if(this.channel==null){
            synchronized(SingleChannelFactory.class){
                this.channel=channel;
            }
        }
    }
    public Channel getChannel(){
        if(channel==null)
            return null;
        return channel;
    }

    public void clear(){
        channel = null;
    }
}
