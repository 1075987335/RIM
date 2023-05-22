package com.example.service.strategy.command.model;


import com.example.common.proto.IM_Message;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

/**
 * @author BanTanger 半糖
 * @Date 2023/4/6 10:28
 */
@Data
public class CommandExecutionRequest {

    private ChannelHandlerContext ctx;

    private IM_Message msg;

    private Integer brokeId;

    //private FeignMessageService feignMessageService;

}
