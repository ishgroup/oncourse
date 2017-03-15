package ish.oncourse.solr.reindex

import groovy.transform.CompileStatic

import java.util.concurrent.TimeUnit

@CompileStatic
class ReindexCoursesJob implements IReindexJob {
    private ScheduleConfig config = new ScheduleConfig(0, 1, TimeUnit.HOURS)

    @Override
    ScheduleConfig getConfig() {
        return config
    }

    @Override
    void run() {
        println "ReindexCoursesJob ${new Date()}"
    }

    static ReindexCoursesJob instance() {
        ReindexCoursesJob job = new ReindexCoursesJob()
        return job
    }
}
