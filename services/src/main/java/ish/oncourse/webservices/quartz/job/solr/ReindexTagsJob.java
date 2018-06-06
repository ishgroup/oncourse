package ish.oncourse.webservices.quartz.job.solr;

import ish.oncourse.solr.reindex.ReindexTags;

public class ReindexTagsJob extends AReindexCollectionJob {
    @Override
    protected void execute0() {
        new ReindexTags(serverRuntime.newContext(), solrClient).run();
    }
}
