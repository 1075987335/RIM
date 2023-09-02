package com.example.config;

import com.example.common.serializer.kyro.KryoSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SerializeConfig {

    @Bean("kryoSerializer")
    public KryoSerializer serializer() {
        return new KryoSerializer();
    }
}
