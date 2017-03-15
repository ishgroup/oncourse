package ish.oncourse.solr.reindex

import groovy.transform.CompileStatic

@CompileStatic
class ReindexCoursesJob implements IReindexJob {
    private ScheduleConfig config

    @Override
    ScheduleConfig getConfig() {
        return config
    }

    @Override
    void run() {
        println "ReindexCoursesJob ${new Date()}"
    }
}
