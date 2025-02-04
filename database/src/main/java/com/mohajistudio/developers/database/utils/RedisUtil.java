package com.mohajistudio.developers.database.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, String> redisTemplate;

    // Redis에 값 저장 (TTL 설정)
    public void setValue(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    // Redis에서 값 조회
    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // Redis에서 키 존재 여부 확인
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // Redis에서 키 삭제
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }
}
