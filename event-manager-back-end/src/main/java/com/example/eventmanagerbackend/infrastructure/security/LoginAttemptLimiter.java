package com.example.eventmanagerbackend.infrastructure.security;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// shortcut: in-memory per instance, move to a shared store (e.g. Redis) if the app scales horizontally
@Component
public class LoginAttemptLimiter {

    static final int MAX_FAILURES = 5;
    static final Duration WINDOW = Duration.ofMinutes(15);
    private static final int PURGE_THRESHOLD = 10_000;

    private record Attempts(int failures, Instant windowStart) {
    }

    private final Map<String, Attempts> attempts = new ConcurrentHashMap<>();
    private final Clock clock;

    public LoginAttemptLimiter() {
        this(Clock.systemUTC());
    }

    LoginAttemptLimiter(Clock clock) {
        this.clock = clock;
    }

    public boolean isBlocked(String username) {
        Attempts current = attempts.get(username);
        return current != null && !isExpired(current) && current.failures() >= MAX_FAILURES;
    }

    public void recordFailure(String username) {
        if (attempts.size() > PURGE_THRESHOLD) {
            attempts.values().removeIf(this::isExpired);
        }
        attempts.compute(username, (key, current) -> current == null || isExpired(current)
                ? new Attempts(1, clock.instant())
                : new Attempts(current.failures() + 1, current.windowStart()));
    }

    public void reset(String username) {
        attempts.remove(username);
    }

    private boolean isExpired(Attempts current) {
        return !clock.instant().isBefore(current.windowStart().plus(WINDOW));
    }
}
