package ish.oncourse.solr.reindex

import ish.oncourse.scheduler.job.Config
import ish.oncourse.scheduler.job.IJob
import org.apache.cayenne.ObjectContext
import org.apache.solr.client.solrj.SolrClient

import java.util.concurrent.TimeUnit

import static ish.oncourse.solr.functions.tag.Functions.getSolrTags

class ReindexTagsJob implements IJob {

    private ObjectContext objectContext
    private SolrClient solrClient
    private Config config = new Config(0, 15, TimeUnit.MINUTES)

    ReindexTagsJob(ObjectContext objectContext, SolrClient solrClient) {
        this.objectContext = objectContext
        this.solrClient = solrClient
    }

    @Override
    Config getConfig() {
        return config
    }

    @Override
    void run() {
        getSolrTags(objectContext).blockingSubscribe({ solrClient.addBean(it) })
        solrClient.commit()
    }
}
