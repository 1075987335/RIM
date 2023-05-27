package com.example.route.config;

import com.example.common.serializer.kyro.KryoSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SerializeConfig {

    @Bean
    public KryoSerializer kryoSerializer(){
        return new KryoSerializer();
    }
}
