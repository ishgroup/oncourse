package ish.oncourse.scheduler.zookeeper

import org.apache.zookeeper.server.ServerCnxnFactory
import org.apache.zookeeper.server.ZooKeeperServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ZookeeperExecutorTest {
    private int zooKeeperPort = 2181
    private int maxClientConnections = 10
    private ZooKeeperServer zkServer

    @Before
    void before() {
        ServerCnxnFactory cnxnFactory = ServerCnxnFactory.createFactory()
        cnxnFactory.configure(new InetSocketAddress(zooKeeperPort), maxClientConnections)
        File home = File.createTempDir()
        zkServer = new ZooKeeperServer(new File(home, 'snap'), new File(home, 'log'), 200)
        cnxnFactory.startup(zkServer)
    }

    @Test
    void test() {
        ZookeeperExecutor executor1 = ZookeeperExecutor.valueOf("127.0.0.1:$zooKeeperPort", "/ish")
        ZookeeperExecutor executor2 = ZookeeperExecutor.valueOf("127.0.0.1:$zooKeeperPort", "/ish")

        executor1.execute({
            Assert.assertTrue(true)
        })

        executor1.execute({
            Assert.assertTrue(false)
        })


        executor1.release()

        executor2.execute({
            Assert.assertTrue(true)
        })

        executor2.release()
    }

    @After
    void after() {
        zkServer.shutdown()
    }
}
