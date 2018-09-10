/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.quartz.job.solr;

import ish.oncourse.solr.reindex.ReindexCourses;
import ish.oncourse.solr.reindex.ReindexSuburbs;

public class ReindexCoursesJob extends AReindexCollectionJob {
    @Override
    protected void execute0() {
        new ReindexCourses(serverRuntime.newContext(), solrClient, courseService::getAvailableSiteKeys).run();
    }
}