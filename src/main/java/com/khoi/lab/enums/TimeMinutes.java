package com.khoi.lab.enums;

/**
 * Equivalence of time units in minutes
 */
public enum TimeMinutes {
    HOUR(Long.valueOf(60)),
    DAY(Long.valueOf(60 * 24)),
    WEEK(Long.valueOf(60 * 24 * 7)),
    MONTH(Long.valueOf(60 * 24 * 30)),
    QUARTER(Long.valueOf(60 * 24 * 30 * 3)),
    YEAR(Long.valueOf(60 * 24 * 365));

    private final Long minutes;

    private TimeMinutes(Long minutes) {
        this.minutes = minutes;
    }

    public Long getMinutes() {
        return minutes;
    }
}
