package com.example.service.strategy.command.system;

import com.example.common.model.RIMUserInfo;
import com.example.common.proto.Header;
import com.example.common.proto.IM_Message;
import com.example.service.strategy.command.CommandStrategy;
import com.example.service.strategy.command.model.CommandExecutionRequest;
import com.example.service.utils.UserChannelFactory;
import io.netty.channel.ChannelHandlerContext;

public class LoginCommand implements CommandStrategy {
    @Override
    public void systemStrategy(CommandExecutionRequest commandExecutionRequest) {
        ChannelHandlerContext ctx=commandExecutionRequest.getCtx();
        IM_Message msg=commandExecutionRequest.getMsg();
        Header header=msg.getHeader();
        RIMUserInfo userInfo=new RIMUserInfo();
        userInfo.setUID(header.getUID());
        UserChannelFactory.put(header.getUID(),ctx.channel());
    }
}
