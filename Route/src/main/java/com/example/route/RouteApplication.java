package com.example.route;


import com.example.route.mq.MQSend;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class RouteApplication{

    @Autowired
    MQSend send;

    public static void main(String[] args) {
        SpringApplication.run(RouteApplication.class, args);
    }

}
