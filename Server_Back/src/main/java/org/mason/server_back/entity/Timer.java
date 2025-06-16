package org.mason.server_back.entity;

import java.time.Duration;
import java.time.LocalDateTime;

public class Timer {
    private LocalDateTime startTime;
    private long duration;

    public Timer() {
        this.startTime = LocalDateTime.now();
        this.duration = 0;
    }

    public void start() {
        this.startTime = LocalDateTime.now();
    }

    public void stop() {
        LocalDateTime end= LocalDateTime.now();
        this.duration = Duration.between(startTime, end).toMillis();
    }
    public LocalDateTime getstart() {
        return startTime;
    }

    public long getDuration() {
        return Duration.between(startTime, LocalDateTime.now()).toMillis();
    }

    public long getFixedDuration() {
        return duration;
    }

    public void reset() {
        this.startTime = LocalDateTime.now();
        this.duration = 0;
    }
}
