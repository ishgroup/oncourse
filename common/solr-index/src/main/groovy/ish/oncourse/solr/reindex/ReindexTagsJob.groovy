package ish.oncourse.solr.reindex

import groovy.transform.CompileStatic
import ish.oncourse.scheduler.ScheduleConfig
import ish.oncourse.scheduler.job.IJob
import org.apache.cayenne.ObjectContext
import org.apache.solr.client.solrj.SolrClient

import java.util.concurrent.TimeUnit

import static ish.oncourse.solr.functions.tag.Functions.getSolrTags

@CompileStatic
class ReindexTagsJob implements IJob {

    private ObjectContext objectContext
    private SolrClient solrClient
    private ScheduleConfig config = new ScheduleConfig(0, 1, TimeUnit.DAYS)

    ReindexTagsJob(ObjectContext objectContext, SolrClient solrClient) {
        this.objectContext = objectContext
        this.solrClient = solrClient
    }

    @Override
    ScheduleConfig getConfig() {
        return config
    }

    @Override
    void run() {
        getSolrTags(objectContext).subscribe({ solrClient.addBean(it) })
        solrClient.commit()
    }
}
