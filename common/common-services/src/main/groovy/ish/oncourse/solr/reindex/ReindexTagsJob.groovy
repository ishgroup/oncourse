package ish.oncourse.solr.reindex

import groovy.transform.CompileStatic

import java.util.concurrent.TimeUnit

@CompileStatic
class ReindexTagsJob implements IReindexJob {
    private ScheduleConfig config = new ScheduleConfig(0, 1, TimeUnit.DAYS)

    @Override
    ScheduleConfig getConfig() {
        return config
    }

    @Override
    void run() {
        println "${getClass().simpleName} ${new Date()}"
    }

    static ReindexTagsJob instance() {
        ReindexTagsJob job = new ReindexTagsJob()
        return job
    }
}
