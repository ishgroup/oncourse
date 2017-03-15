package ish.oncourse.solr.reindex

import groovy.transform.CompileStatic

@CompileStatic
interface IReindexJob extends Runnable {
    ScheduleConfig getConfig()
}
