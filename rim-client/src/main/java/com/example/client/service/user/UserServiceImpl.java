package com.example.client.service.user;

import com.example.RouteUserService;
import com.example.client.config.UserInfo;
import com.example.client.request.LoginVO;
import com.example.client.request.OfflineReqVO;
import com.example.client.response.ServerInfo;
import com.example.client.service.send.SendMessage;
import com.example.client.service.UserService;
import com.example.common.constant.Constants;
import com.example.common.proto.Header;
import com.example.common.proto.IM_Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "UserServiceImpl")
public class UserServiceImpl implements UserService {

    @DubboReference
    private RouteUserService routeUserService;

    @Autowired
    private SendMessage sendMessage;

    @Autowired
    private UserInfo userInfo;

    @Override
    public ServerInfo login(LoginVO loginVO) {
        try{
            return routeUserService.Login(loginVO);
        }
        catch(Exception e){
            log.error("用户{}登陆失败！", loginVO.getUID());
        }
        return null;
    }

    @Override
    public void loginServer() {
        IM_Message IMMessage =new IM_Message();
        Header header=new Header();
        header.setType(Constants.CommandType.LOGIN);
        header.setUID(userInfo.getUserID());
        IMMessage.setHeader(header);
        sendMessage.send(IMMessage);
    }

    @Override
    public void offline() {
        OfflineReqVO offlineReqVO = new OfflineReqVO();
        offlineReqVO.setUID(userInfo.getUserID());
        try {
            routeUserService.offLine(offlineReqVO);
            log.info("用户{}离线成功",userInfo.getUserID());
        } catch (Exception e) {
            log.error("用户{}离线失败！",userInfo.getUserID());
            throw new RuntimeException(e);
        }
    }
}
