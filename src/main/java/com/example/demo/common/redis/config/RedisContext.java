package com.example.demo.common.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.GenericToStringSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;

//@Configuration
public class RedisContext {

//    @Bean
//    RedisTemplate<String, Serializable> redisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, Serializable> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(connectionFactory);
//
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//
//        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Serializable.class));
//        redisTemplate.setHashValueSerializer(new GenericToStringSerializer<>(Long.class));
//
//        return redisTemplate;
//    }

}
