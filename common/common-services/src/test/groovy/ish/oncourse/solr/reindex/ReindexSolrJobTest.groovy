package ish.oncourse.solr.reindex

import ish.oncourse.solr.zookeeper.ZookeeperLock
import org.junit.Test
import org.mockito.Mockito

import java.util.concurrent.TimeUnit

/**
 * Created by akoira on 14/3/17.
 */
class ReindexSolrJobTest {

    @Test
    void test() {

        ZookeeperLock zookeeperLock = Mockito.mock(ZookeeperLock)
        SolrReindexService.valueOf(zookeeperLock, new IReindexJob() {
            @Override
            ScheduleConfig getConfig() {
                return new ScheduleConfig().with {
                    initialDelay = 0
                    period = 1
                    timeUnit = TimeUnit.SECONDS
                    it
                }
            }

            @Override
            void run() {
                println "ReindexSolrJobTest:${new Date()}"
            }
        })
    }
}
