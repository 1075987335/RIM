package com.example.route.config;

import com.example.common.constant.Constants;
import com.example.route.mq.MQCallBack;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class MqConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    MQCallBack mqCallBack;
    @Bean
    public void setCallBackAndReturnBack(){
        rabbitTemplate.setReturnsCallback(mqCallBack);
        rabbitTemplate.setConfirmCallback(mqCallBack);
    }

    //指定相应功能的队列
    @Bean
    public Queue p2pMessageQueue(){
        return new Queue(Constants.RabbitmqConstants.P2PMessage);
    }

    @Bean
    public Queue p2pACKQueue(){
        return new Queue(Constants.RabbitmqConstants.P2PACK);
    }

    @Bean
    public Queue groupMessageQueue(){
        return new Queue(Constants.RabbitmqConstants.GroupMessage);
    }

    @Bean
    public Queue groupACKQueue(){
        return new Queue(Constants.RabbitmqConstants.GroupACK);
    }

    @Bean
    public Queue p2pOfflineQueue(){
        return new Queue(Constants.RabbitmqConstants.P2POfflineMessage);
    }

    //指定相应功能的交换机
    @Bean
    TopicExchange exchange(){
        return new TopicExchange(Constants.RabbitmqConstants.Routing);
    }


    //将队列与交换机进行绑定
    @Bean
    Binding bindingP2POfflineQueue(){
        return BindingBuilder.bind(p2pOfflineQueue()).to(exchange()).with(Constants.RabbitmqConstants.P2POfflineMessage);
    }

    @Bean
    Binding bindingP2PACKQueue(){
        return BindingBuilder.bind(p2pACKQueue()).to(exchange()).with(Constants.RabbitmqConstants.P2PACK);
    }

    @Bean
    Binding bindingP2PMessageQueue(){
        return BindingBuilder.bind(p2pMessageQueue()).to(exchange()).with(Constants.RabbitmqConstants.P2PMessage);
    }

    @Bean
    Binding bindingGroupMessageQueue(){
        return BindingBuilder.bind(groupMessageQueue()).to(exchange()).with(Constants.RabbitmqConstants.GroupMessage);
    }

    @Bean
    Binding bindingGroupACKQueue(){
        return BindingBuilder.bind(groupACKQueue()).to(exchange()).with(Constants.RabbitmqConstants.GroupACK);
    }
}
