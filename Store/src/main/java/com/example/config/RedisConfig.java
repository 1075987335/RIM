package com.example.config;

import com.example.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Autowired
    RedisTemplate redisTemplate;

//    @Autowired
//    RedisZSETAddListener zsetAddListener;

    @Bean
    public RedisUtil redisUtil(){
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        //key序列化方式
        redisTemplate.setKeySerializer(redisSerializer);
        //key haspmap序列化
        redisTemplate.setHashKeySerializer(redisSerializer);
        //value hashmap序列化
        redisTemplate.setHashValueSerializer(redisSerializer);
        return new RedisUtil(this.redisTemplate);
    }

//    @Bean
//    public RedisMessageListenerContainer container(){
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
//        container.setConnectionFactory(connectionFactory);
//        //监听所有的key的set事件
//        container.addMessageListener(zsetAddListener, zsetAddListener.getTopic());
//        return container;
//    }
}
