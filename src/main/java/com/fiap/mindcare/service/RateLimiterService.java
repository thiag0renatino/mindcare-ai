package com.fiap.mindcare.service;

import com.fiap.mindcare.service.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;

    @Value("${mindcheck.ai.rate-limit.max-requests:5}")
    private int maxRequests;

    @Value("${mindcheck.ai.rate-limit.window-minutes:60}")
    private int windowMinutes;

    public RateLimiterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void checkRateLimit(Long userId) {
        String key = "rate_limit:mindcheck:" + userId;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, windowMinutes, TimeUnit.MINUTES);
        }
        if (count != null && count > maxRequests) {
            throw new BusinessException("Limite de análises atingido. Tente novamente mais tarde.");
        }
    }
}
