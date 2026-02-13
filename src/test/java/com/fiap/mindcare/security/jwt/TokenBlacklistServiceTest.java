package com.fiap.mindcare.security.jwt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    @Test
    void blacklist_shouldStoreTokenInRedisWithTtl() {
        String token = "some-jwt-token";
        long futureMillis = System.currentTimeMillis() + 3600_000;
        Date futureDate = new Date(futureMillis);
        when(tokenProvider.getExpirationDate(token)).thenReturn(futureDate);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        tokenBlacklistService.blacklist(token);

        verify(valueOperations).set(eq(token), eq("blacklisted"), anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    void blacklist_shouldNotStoreTokenIfAlreadyExpired() {
        String token = "expired-token";
        Date pastDate = new Date(System.currentTimeMillis() - 1000);
        when(tokenProvider.getExpirationDate(token)).thenReturn(pastDate);

        tokenBlacklistService.blacklist(token);

        verify(redisTemplate, never()).opsForValue();
    }

    @Test
    void isBlacklisted_shouldReturnTrueWhenKeyExists() {
        String token = "blacklisted-token";
        when(redisTemplate.hasKey(token)).thenReturn(Boolean.TRUE);

        assertTrue(tokenBlacklistService.isBlacklisted(token));
    }

    @Test
    void isBlacklisted_shouldReturnFalseWhenKeyDoesNotExist() {
        String token = "unknown-token";
        when(redisTemplate.hasKey(token)).thenReturn(Boolean.FALSE);

        assertFalse(tokenBlacklistService.isBlacklisted(token));
    }

    @Test
    void isBlacklisted_shouldReturnFalseWhenHasKeyReturnsNull() {
        String token = "some-token";
        when(redisTemplate.hasKey(token)).thenReturn(null);

        assertFalse(tokenBlacklistService.isBlacklisted(token));
    }
}
