package com.example.common.util;

import com.example.common.constant.Constants;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class NettyAttrUtil {

    private static final AttributeKey<Long> READ_TIME = AttributeKey.valueOf(Constants.ChannelConstants.ReadTime);

    /**
     * 更新时间
     *
     * @param channel
     * @param time
     */
    public static void updateReadTime(Channel channel, Long time) {
        channel.attr(READ_TIME).set(time);
    }


    /**
     * 得到上次读的时间
     *
     * @param channel
     * @return
     */
    public static Long getReadTime(Channel channel) {
        Long time = channel.attr(READ_TIME).get();

        if (time == null) {
            return null;
        }
        return time;
    }


}
