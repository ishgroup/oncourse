package ish.oncourse.solr.reindex

import groovy.transform.CompileStatic
import org.apache.cayenne.ObjectContext
import org.apache.solr.client.solrj.SolrClient

import java.util.concurrent.TimeUnit

import static ish.oncourse.solr.functions.suburb.Functions.getSolrSuburbs

@CompileStatic
class ReindexSuburbsJob implements IReindexJob {
    private ObjectContext objectContext
    private SolrClient solrClient
    private ScheduleConfig config = new ScheduleConfig(0, 1, TimeUnit.DAYS)

    ReindexSuburbsJob(ObjectContext objectContext, SolrClient solrClient) {
        this.objectContext = objectContext
        this.solrClient = solrClient
    }

    @Override
    ScheduleConfig getConfig() {
        return config
    }

    @Override
    void run() {
        getSolrSuburbs(objectContext).each { solrClient.addBean(it) }
    }
}
