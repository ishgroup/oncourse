package ish.oncourse.solr.reindex

import ish.oncourse.solr.functions.tag.Functions
import ish.oncourse.solr.model.STag
import org.apache.cayenne.ObjectContext
import org.apache.solr.client.solrj.SolrClient

class ReindexTagsJob extends ReindexJob<STag> {
    ReindexTagsJob(ObjectContext objectContext, SolrClient solrClient, String cron = "0 00 6 ? * *") {
        super(solrClient,
                Functions.getSolrTags(objectContext),
                cron)
    }
}
