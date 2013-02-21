package com.idamobile.vpb.courier.security;

import android.os.SystemClock;

public class SecurityPreferences {

    private final long confidenceInterval;
    private long confidenceIntervalStartTime;
    private boolean started;

    public SecurityPreferences(long confidenceInterval) {
        this.confidenceInterval = confidenceInterval;
    }

    public boolean isConfidenceIntervalFinished() {
        long curTime = SystemClock.elapsedRealtime();
        long diff = curTime - confidenceIntervalStartTime;
        return diff < 0 || diff > confidenceInterval;
    }

    public boolean isConfidenceIntervalWasStarted() {
        return started;
    }

    public void startConfidenceInterval() {
        confidenceIntervalStartTime = SystemClock.elapsedRealtime();
        started = true;
    }

    public void stopConfidenceInterval() {
        confidenceIntervalStartTime = SystemClock.elapsedRealtime();
        started = false;
    }

}