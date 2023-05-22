package com.example.route.config;

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
    @Bean
    public RedisUtil redisUtil(){
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        //key序列化方式
        redisTemplate.setKeySerializer(redisSerializer);
//        //value序列化
//        redisTemplate.setValueSerializer(redisSerializer);
//        //value hashmap序列化
//        redisTemplate.setHashValueSerializer(redisSerializer);
        //key haspmap序列化
//        redisTemplate.setHashKeySerializer(redisSerializer);

        return new RedisUtil(this.redisTemplate);
    }
}
