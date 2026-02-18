package com.fiap.mindcare.service;

import com.fiap.mindcare.service.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimiterServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private RateLimiterService rateLimiterService;

    private static final String KEY = "rate_limit:mindcheck:1";

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        rateLimiterService = new RateLimiterService(redisTemplate);
        ReflectionTestUtils.setField(rateLimiterService, "maxRequests", 5);
        ReflectionTestUtils.setField(rateLimiterService, "windowMinutes", 60);
    }

    @Test
    void shouldAllowRequestWithinLimit() {
        when(valueOperations.increment(KEY)).thenReturn(3L);

        assertDoesNotThrow(() -> rateLimiterService.checkRateLimit(1L));
        verify(redisTemplate, never()).expire(KEY, 60, TimeUnit.MINUTES);
    }

    @Test
    void shouldSetExpireOnFirstRequest() {
        when(valueOperations.increment(KEY)).thenReturn(1L);

        assertDoesNotThrow(() -> rateLimiterService.checkRateLimit(1L));
        verify(redisTemplate).expire(KEY, 60, TimeUnit.MINUTES);
    }

    @Test
    void shouldThrowWhenLimitExceeded() {
        when(valueOperations.increment(KEY)).thenReturn(6L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> rateLimiterService.checkRateLimit(1L));
        assert ex.getMessage().contains("Limite de análises atingido");
    }

    @Test
    void shouldAllowExactlyAtLimit() {
        when(valueOperations.increment(KEY)).thenReturn(5L);

        assertDoesNotThrow(() -> rateLimiterService.checkRateLimit(1L));
    }
}
