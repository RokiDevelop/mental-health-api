package com.kiryukhin.mental_health.configs;

import com.kiryukhin.mental_health.models.Token;
import com.kiryukhin.mental_health.models.VerificationToken;
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

    @Bean
    public RedisTemplate<String, VerificationToken> verificationTokenRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, VerificationToken> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
