package ish.oncourse.solr.zookeeper

import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.KeeperException
import org.apache.zookeeper.ZooDefs
import org.apache.zookeeper.ZooKeeper
import org.apache.zookeeper.data.ACL
import org.apache.zookeeper.server.ServerCnxnFactory
import org.apache.zookeeper.server.ZooKeeperServer
import org.junit.Assert
import org.junit.Test

class ZookeeperInstanceLockTest {
    private int zooKeeperPort = 2181
    private int maxClientConnections = 10


    @Test
    void test() {

        ServerCnxnFactory cnxnFactory = ServerCnxnFactory.createFactory()
        cnxnFactory.configure(new InetSocketAddress(zooKeeperPort), maxClientConnections)


        File home = File.createTempDir()
        ZooKeeperServer zkServer = new ZooKeeperServer(new File(home, 'snap'), new File(home, 'log'), 200)
        cnxnFactory.startup(zkServer)

        Lock instance1 = new Lock()
        Lock instance2 = new Lock()

        instance1.lockAndExecute({Assert.assertTrue(true)}, {Exception e -> e?.printStackTrace(); Assert.assertTrue(false)})
        instance2.lockAndExecute({Assert.assertTrue(false)}, {Exception e -> e?.printStackTrace(); Assert.assertTrue(true)})

        instance1.unlock()
        instance2.lockAndExecute({Assert.assertTrue(true)}, {Exception e -> e?.printStackTrace(); Assert.assertTrue(false)})
    }



    class Lock {
        private ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:$zooKeeperPort", 2000, {})

        private String blockPath = "/ish"
        private byte[] bytes = new byte[0]
        private List<ACL> acls = [new ACL(ZooDefs.Perms.ALL, ZooDefs.Ids.ANYONE_ID_UNSAFE)]

        void lockAndExecute(Closure execute, Closure error) {
            boolean result = false
            zooKeeper.create(blockPath, bytes, acls, CreateMode.EPHEMERAL, { int rc, String path, Object ctx, String name ->
                try {
                    KeeperException.Code code = KeeperException.Code.get(rc)
                    switch (code) {
                        case KeeperException.Code.OK:
                            execute.call()
                            break
                        default:
                            println(code)
                            error.call()
                            break
                    }
                } catch (e) {
                    error.call(e)
                }
            }, null)
        }

        void unlock() {
            try {
//                zooKeeper.delete(blockPath, 0)
            } finally {
                zooKeeper.close()
            }
        }
    }


}
