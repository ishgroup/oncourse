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

    private IExecutor[] executors

    private List<ScheduledFuture> futures = new LinkedList<>()

    private ScheduledService() {

    }

    List<ScheduledFuture> getFutures() { futures }

    ScheduledService start() {
        service = Executors.newScheduledThreadPool(executors.size())
        executors.each {
            IExecutor e -> e.execute {
            futures.add(service.scheduleAtFixedRate(e.job(), e.job().config.initialDelay, e.job().config.period, e.job().config.timeUnit)) 
            }
        }
        return this
    }

    void close() {
        try {
            service.addShutdownHook {
                executors.each {IExecutor e -> e.release()}
            }
            service.shutdown()
        } catch (e) {
            logger.warn(e)
        }
    }


    static ScheduledService valueOf(IExecutor... lockExecutors) {
        ScheduledService result = new ScheduledService()
        result.executors = lockExecutors
        return result
    }
}
