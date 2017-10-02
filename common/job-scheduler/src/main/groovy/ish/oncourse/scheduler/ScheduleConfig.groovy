package ish.oncourse.scheduler

import groovy.transform.CompileStatic

import java.util.concurrent.TimeUnit

@CompileStatic
class ScheduleConfig {
    private long initialDelay
    private long period
    private TimeUnit timeUnit

    ScheduleConfig(long initialDelay, long period, TimeUnit timeUnit) {
        this.initialDelay = initialDelay
        this.period = period
        this.timeUnit = timeUnit
    }

    long getInitialDelay() { initialDelay }

    long getPeriod() { period }

    TimeUnit getTimeUnit() { timeUnit }

}
