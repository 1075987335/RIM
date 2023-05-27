package com.example.client;

import com.example.client.service.id.GetIdService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ClientApplication.class)
@Slf4j
class ClientApplicationTests {

    @Autowired
    GetIdService getIdService;

    @Test
    void test(){
        log.info("wo");
    }

    @Test
    void test1(){
        log.info("或得到的id为:{}", getIdService.getNextId("key"));
    }

}
