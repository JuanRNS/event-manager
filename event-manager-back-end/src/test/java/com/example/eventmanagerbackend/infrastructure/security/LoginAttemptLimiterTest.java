package com.example.eventmanagerbackend.infrastructure.security;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginAttemptLimiterTest {

    private final Instant start = Instant.parse("2026-01-01T00:00:00Z");
    private final Clock clock = mock(Clock.class);
    private final LoginAttemptLimiter limiter = new LoginAttemptLimiter(clock);

    @Test
    void blocksAfterMaxFailuresUntilWindowExpires() {
        when(clock.instant()).thenReturn(start);
        for (int i = 0; i < LoginAttemptLimiter.MAX_FAILURES; i++) {
            assertFalse(limiter.isBlocked("ana"));
            limiter.recordFailure("ana");
        }
        assertTrue(limiter.isBlocked("ana"));
        assertFalse(limiter.isBlocked("bia"));

        when(clock.instant()).thenReturn(start.plus(LoginAttemptLimiter.WINDOW).minusSeconds(1));
        assertTrue(limiter.isBlocked("ana"));

        when(clock.instant()).thenReturn(start.plus(LoginAttemptLimiter.WINDOW));
        assertFalse(limiter.isBlocked("ana"));
    }

    @Test
    void resetClearsFailures() {
        when(clock.instant()).thenReturn(start);
        for (int i = 0; i < LoginAttemptLimiter.MAX_FAILURES; i++) {
            limiter.recordFailure("ana");
        }

        limiter.reset("ana");

        assertFalse(limiter.isBlocked("ana"));
    }
}
