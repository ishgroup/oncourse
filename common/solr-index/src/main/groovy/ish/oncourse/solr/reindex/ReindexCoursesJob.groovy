package ish.oncourse.solr.reindex

import io.reactivex.schedulers.Schedulers
import ish.oncourse.solr.functions.course.SCourseFunctions
import org.apache.cayenne.ObjectContext
import org.apache.solr.client.solrj.SolrClient

class ReindexCoursesJob extends ReindexJob {
    ReindexCoursesJob(ObjectContext objectContext, SolrClient solrClient, String cron = "0 0 5 ? * *") {
        super(solrClient,
                SCourseFunctions.SCourses(objectContext, new Date(), Schedulers.io()),
                cron)
    }
}
