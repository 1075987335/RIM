package com.example.client;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ClientApplication.class)
@Slf4j
class ClientApplicationTests {

    @Test
    void test(){
        log.info("wo");
    }

}
