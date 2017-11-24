package ish.oncourse.solr.reindex

import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ish.oncourse.scheduler.ScheduleConfig
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
    private ScheduleConfig config = new ScheduleConfig(0, 1, TimeUnit.HOURS)
    private Disposable disposable
    private Scheduler scheduler
    private Date date

    ReindexCoursesJob(ObjectContext objectContext, SolrClient solrClient, Date date = new Date(), Scheduler scheduler = Schedulers.io()) {
        this.objectContext = objectContext
        this.solrClient = solrClient
        this.scheduler = scheduler
        this.date = date
    }

    @Override
    ScheduleConfig getConfig() {
        return config
    }

    boolean isActive() {
        return disposable.isDisposed()
    }

    @Override
    void run() {
        disposable = SCourseFunctions.SCourses(objectContext, date, scheduler).subscribe(
                { solrClient.addBean(it) },
                { e -> logger.error(e.getLocalizedMessage(), e) },
                { solrClient.commit() })

    }
}
