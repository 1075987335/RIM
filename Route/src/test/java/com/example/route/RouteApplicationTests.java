package com.example.route;

import com.example.MessageSendService;
import com.example.common.util.RedisUtil;
import com.example.route.mq.MQSend;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest(classes = RouteApplication.class)
@Slf4j
class RouteApplicationTests {

    @Autowired
    MQSend send;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    MessageSendService messageSendService;

    @Test
    public void test1(){
        boolean userLoginState = redisUtil.getUserLoginState(1);
        System.out.println(userLoginState);
    }
}
