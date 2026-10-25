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
    // shortcut: an attacker filling the cap blocks new usernames for one window, upgrade to a per-IP or shared-store limiter
    static final int MAX_TRACKED = 100_000;

    private record Attempts(int count, Instant windowStart) {
    }

    private final Map<String, Attempts> attempts = new ConcurrentHashMap<>();
    private final Clock clock;
    private final int maxTracked;

    public LoginAttemptLimiter() {
        this(Clock.systemUTC(), MAX_TRACKED);
    }

    LoginAttemptLimiter(Clock clock, int maxTracked) {
        this.clock = clock;
        this.maxTracked = maxTracked;
    }

    public boolean tryAcquire(String username) {
        if (attempts.size() >= maxTracked) {
            attempts.values().removeIf(this::isExpired);
        }
        boolean[] allowed = {false};
        attempts.compute(username, (key, current) -> {
            if (current == null && attempts.size() >= maxTracked) {
                return null;
            }
            if (current == null || isExpired(current)) {
                allowed[0] = true;
                return new Attempts(1, clock.instant());
            }
            if (current.count() >= MAX_FAILURES) {
                return current;
            }
            allowed[0] = true;
            return new Attempts(current.count() + 1, current.windowStart());
        });
        return allowed[0];
    }

    public void reset(String username) {
        attempts.remove(username);
    }

    private boolean isExpired(Attempts current) {
        return !clock.instant().isBefore(current.windowStart().plus(WINDOW));
    }
}
