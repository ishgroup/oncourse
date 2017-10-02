package ish.oncourse.scheduler

import groovy.transform.CompileStatic
import ish.oncourse.scheduler.job.IJob
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture

/**
 * The service is used to start reindex jobs periodically
 */
@CompileStatic
class ScheduledService {
    private static final Logger logger = LogManager.getLogger()

    private ScheduledExecutorService service

    private IExecutor executor

    private List<IJob> jobs = new LinkedList<>()

    private List<ScheduledFuture> futures = new LinkedList<>()

    private ScheduledService() {

    }

    List<ScheduledFuture> getFutures() { futures }

    ScheduledService start() {
        this.executor.execute({
            service = Executors.newScheduledThreadPool(3)
            jobs.each {
                futures.add(service.scheduleAtFixedRate(it, it.config.initialDelay, it.config.period, it.config.timeUnit))
            }

        })
        return this
    }

    void close() {
        try {
            service.addShutdownHook {
                this.executor.release()
            }
            service.shutdown()
        } catch (e) {
            logger.warn(e)
        }
    }


    static ScheduledService valueOf(IExecutor lockExecutor, IJob... jobs) {
        ScheduledService result = new ScheduledService()
        result.executor = lockExecutor
        result.jobs.addAll(jobs)
        return result
    }
}
