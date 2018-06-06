package ish.oncourse.webservices.quartz.job.solr;

import ish.oncourse.solr.reindex.ReindexCourses;

public class ReindexCoursesJob extends AReindexCollectionJob {
    @Override
    protected void execute0() {
        new ReindexCourses(serverRuntime.newContext(), solrClient).run();
    }
}
