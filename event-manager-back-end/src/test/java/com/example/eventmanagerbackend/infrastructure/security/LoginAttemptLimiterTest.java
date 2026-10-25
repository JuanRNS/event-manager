package com.example.eventmanagerbackend.infrastructure.security;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginAttemptLimiterTest {

    private final Instant start = Instant.parse("2026-01-01T00:00:00Z");
    private final Clock clock = mock(Clock.class);
    private final LoginAttemptLimiter limiter = new LoginAttemptLimiter(clock, LoginAttemptLimiter.MAX_TRACKED);

    @Test
    void blocksAfterMaxFailuresUntilWindowExpires() {
        when(clock.instant()).thenReturn(start);
        for (int i = 0; i < LoginAttemptLimiter.MAX_FAILURES; i++) {
            assertTrue(limiter.tryAcquire("ana"));
        }
        assertFalse(limiter.tryAcquire("ana"));
        assertTrue(limiter.tryAcquire("bia"));

        when(clock.instant()).thenReturn(start.plus(LoginAttemptLimiter.WINDOW).minusSeconds(1));
        assertFalse(limiter.tryAcquire("ana"));

        when(clock.instant()).thenReturn(start.plus(LoginAttemptLimiter.WINDOW));
        assertTrue(limiter.tryAcquire("ana"));
    }

    @Test
    void resetClearsAttempts() {
        when(clock.instant()).thenReturn(start);
        for (int i = 0; i < LoginAttemptLimiter.MAX_FAILURES; i++) {
            limiter.tryAcquire("ana");
        }

        limiter.reset("ana");

        assertTrue(limiter.tryAcquire("ana"));
    }

    @Test
    void concurrentBurstAllowsExactlyMaxFailures() throws Exception {
        when(clock.instant()).thenReturn(start);
        int threads = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch gate = new CountDownLatch(1);
        List<Future<Boolean>> results = new ArrayList<>();
        try {
            for (int i = 0; i < threads; i++) {
                results.add(executor.submit(() -> {
                    gate.await();
                    return limiter.tryAcquire("ana");
                }));
            }
            gate.countDown();
            int allowed = 0;
            for (Future<Boolean> result : results) {
                if (result.get()) {
                    allowed++;
                }
            }
            assertEquals(LoginAttemptLimiter.MAX_FAILURES, allowed);
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void failsClosedForNewUsernamesWhenCapIsFull() {
        LoginAttemptLimiter capped = new LoginAttemptLimiter(clock, 2);
        when(clock.instant()).thenReturn(start);
        assertTrue(capped.tryAcquire("ana"));
        assertTrue(capped.tryAcquire("bia"));

        assertFalse(capped.tryAcquire("caio"));
        assertTrue(capped.tryAcquire("ana"));

        when(clock.instant()).thenReturn(start.plus(LoginAttemptLimiter.WINDOW));
        assertTrue(capped.tryAcquire("caio"));
    }
}
