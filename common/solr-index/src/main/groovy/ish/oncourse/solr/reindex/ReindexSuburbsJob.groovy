package ish.oncourse.solr.reindex

import ish.oncourse.solr.model.SSuburb
import org.apache.cayenne.ObjectContext
import org.apache.solr.client.solrj.SolrClient

import static ish.oncourse.solr.functions.suburb.Functions.getSolrSuburbs

class ReindexSuburbsJob extends ReindexJob<SSuburb> {
    ReindexSuburbsJob(ObjectContext objectContext, SolrClient solrClient, String cron = "0 30 5 ? * *") {
        super(solrClient,
                getSolrSuburbs(objectContext),
                cron)
    }
}
