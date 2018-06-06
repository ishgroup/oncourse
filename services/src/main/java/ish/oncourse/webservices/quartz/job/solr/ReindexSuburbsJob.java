package ish.oncourse.webservices.quartz.job.solr;

import ish.oncourse.solr.reindex.ReindexSuburbs;

public class ReindexSuburbsJob extends AReindexCollectionJob {
    @Override
    protected void execute0() {
        new ReindexSuburbs(serverRuntime.newContext(), solrClient).run();
    }
}
