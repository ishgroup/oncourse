package ish.oncourse.solr.reindex

import groovy.transform.CompileStatic
import ish.oncourse.scheduler.ScheduleConfig
import ish.oncourse.scheduler.job.IJob
import ish.oncourse.solr.functions.course.SCourseFunctions
import org.apache.cayenne.ObjectContext
import org.apache.solr.client.solrj.SolrClient

import java.util.concurrent.TimeUnit

@CompileStatic
class ReindexCoursesJob implements IJob {
    private ObjectContext objectContext
    private SolrClient solrClient
    private ScheduleConfig config = new ScheduleConfig(0, 1, TimeUnit.HOURS)

    ReindexCoursesJob(ObjectContext objectContext, SolrClient solrClient) {
        this.objectContext = objectContext
        this.solrClient = solrClient
    }

    @Override
    ScheduleConfig getConfig() {
        return config
    }

    @Override
    void run() {
        SCourseFunctions.SCourses.call(objectContext).subscribe({
            solrClient.addBean(it)
        })
        solrClient.commit()
    }
}
