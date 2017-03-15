package ish.oncourse.solr.reindex

import groovy.transform.CompileStatic

import java.util.concurrent.TimeUnit

@CompileStatic
class ScheduleConfig {
    long initialDelay
    long period
    TimeUnit timeUnit
}
