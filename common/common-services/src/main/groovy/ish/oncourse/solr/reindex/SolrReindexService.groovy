package ish.oncourse.solr.reindex

import groovy.transform.CompileStatic
import ish.oncourse.solr.zookeeper.ZookeeperLock
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture

@CompileStatic
class SolrReindexService {
    private static final Logger logger = LogManager.getLogger()

    private ScheduledExecutorService service

    private ZookeeperLock zookeeperLock

    List<IReindexJob> jobs = new LinkedList<>()

    private List<ScheduledFuture> futures = new LinkedList<>();


    private SolrReindexService() {

    }

    void start() {
        this.zookeeperLock.lockAndExecute({
            service = Executors.newScheduledThreadPool(3)
            jobs.each {
                futures.add(service.scheduleAtFixedRate(it, it.config.initialDelay, it.config.period, it.config.timeUnit))
            }

        }, { Exception e -> logger.error("Cannot start SolrReindexService", e) })
    }

    void close() {
        try {
            service.shutdown()
        } catch (e) {
            logger.warn(e)
        }
    }


    static SolrReindexService valueOf(ZookeeperLock zookeeperLock, IReindexJob... jobs) {
        SolrReindexService result = new SolrReindexService()
        result.jobs.addAll(jobs)
        return result
    }
}
