package com.kiryukhin.mental_health.configs;

import com.kiryukhin.mental_health.models.Token;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfigs {


    @Bean
    public RedisTemplate<String, Token> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Token> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
