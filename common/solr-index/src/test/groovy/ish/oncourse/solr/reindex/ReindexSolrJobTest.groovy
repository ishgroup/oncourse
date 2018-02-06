package ish.oncourse.solr.reindex

import ish.oncourse.scheduler.IExecutor
import ish.oncourse.scheduler.ScheduleConfig
import ish.oncourse.scheduler.ScheduledService
import ish.oncourse.scheduler.job.IJob
import org.junit.Assert
import org.junit.Test

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class ReindexSolrJobTest {

    @Test
    void test() {
        AtomicInteger count = new AtomicInteger(0)
        IExecutor executor = new IExecutor() {
            @Override
            void execute(Closure execute) {
                execute.call()
            }

            @Override
            void release() {
            }

            @Override
            IJob job() {
                
                return new IJob() {
                    @Override
                    ScheduleConfig getConfig() {
                        return new ScheduleConfig(0, 1, TimeUnit.SECONDS)
                    }

                    @Override
                    void run() {
                        count.incrementAndGet()
                        println "ReindexSolrJobTest:${new Date()}"
                    }
                }
            }
        }


        
        ScheduledService.valueOf(executor).start()


        while (count.get() < 5) {
            sleep(1000)
        }
        Assert.assertTrue(count.get() >= 5)
    }
}
