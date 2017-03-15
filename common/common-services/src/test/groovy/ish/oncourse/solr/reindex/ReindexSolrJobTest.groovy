package ish.oncourse.solr.reindex

import ish.oncourse.services.IExecutor
import ish.oncourse.services.ScheduledService
import ish.oncourse.services.zookeeper.ZookeeperExecutor
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

import java.util.concurrent.TimeUnit

class ReindexSolrJobTest {

    @Test
    void test() {
        IExecutor executor = new IExecutor() {
            @Override
            void execute(Closure execute) {
                execute.call()
            }

            @Override
            void release() {
            }
        }


        int count = 0
        ScheduledService service = ScheduledService.valueOf(executor, new IReindexJob() {
            @Override
            ScheduleConfig getConfig() {
                return new ScheduleConfig(0, 1, TimeUnit.SECONDS)
            }

            @Override
            void run() {
                count++
                println "ReindexSolrJobTest:${new Date()}"
            }
        }).start()


        while (count < 5) {
            sleep(1000)
        }
        Assert.assertEquals(5, count)
    }
}
