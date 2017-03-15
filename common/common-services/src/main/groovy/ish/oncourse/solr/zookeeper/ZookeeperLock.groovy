package ish.oncourse.solr.zookeeper

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.zookeeper.*
import org.apache.zookeeper.data.ACL

class ZookeeperLock {
    private static final Logger logger = LogManager.getLogger()

    private String nodePath
    private ZooKeeper zooKeeper
    private byte[] bytes = new byte[0]
    private List<ACL> acls = [new ACL(ZooDefs.Perms.ALL, ZooDefs.Ids.ANYONE_ID_UNSAFE)]

    void lockAndExecute(Closure execute, Closure error) {
        this.zooKeeper.create(nodePath, bytes, acls, CreateMode.EPHEMERAL, { int rc, String path, Object ctx, String name ->
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
                logger.warn(e)
                error.call(e)
            }
        }, null)
    }

    static ZookeeperLock valueOf(String connection, String nodePath, int sessionTimeout = 2000) {
        ZookeeperLock lock = new ZookeeperLock()
        lock.zooKeeper = new ZooKeeper(connection, sessionTimeout, { WatchedEvent event ->
            logger.debug(event)
        })
        lock.nodePath = nodePath
        return lock
    }
}
