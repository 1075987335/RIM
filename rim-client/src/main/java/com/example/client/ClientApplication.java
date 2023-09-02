package com.example.client;

import com.example.client.scanner.Scan;
import com.example.client.util.SpringBeanFactory;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ThreadPoolExecutor;

@SpringBootApplication
@EnableDubbo
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
        ThreadPoolExecutor singleJobExecutor = SpringBeanFactory.getBean("singleJobExecutor", ThreadPoolExecutor.class);
        singleJobExecutor.execute(new Thread(new Scan()));
    }
}
