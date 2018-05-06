package ish.oncourse.solr.reindex

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import ish.oncourse.scheduler.job.Config
import ish.oncourse.scheduler.job.IJob
import ish.oncourse.solr.functions.course.SCourseFunctions
import org.apache.cayenne.ObjectContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.solr.client.solrj.SolrClient

import java.util.concurrent.TimeUnit

class ReindexCoursesJob implements IJob {
    private static Logger logger = LogManager.logger

    private ObjectContext objectContext
    private SolrClient solrClient
    private Config config = new Config(0, 1, TimeUnit.SECONDS)
    private Scheduler scheduler
    private Date date

    ReindexCoursesJob(ObjectContext objectContext, SolrClient solrClient, Date date = new Date(), Scheduler scheduler = Schedulers.io()) {
        this.objectContext = objectContext
        this.solrClient = solrClient
        this.scheduler = scheduler
        this.date = date
    }

    @Override
    Config getConfig() {
        return config
    }

    @Override
    void run() {
        SCourseFunctions.SCourses(objectContext, date, scheduler).blockingSubscribe(
                {
                    try {
                        solrClient.addBean(it)
                    } catch (Exception e) {
                        logger.catching(e)
                    }
                },
                { e -> logger.error(e.getLocalizedMessage(), e) },
                {
                    solrClient.commit()
                })
    }
}
