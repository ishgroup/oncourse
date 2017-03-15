package ish.oncourse.services.zookeeper

import ish.oncourse.services.IExecutor
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.zookeeper.*
import org.apache.zookeeper.data.ACL

import java.util.concurrent.atomic.AtomicBoolean

class ZookeeperExecutor implements IExecutor {

    private static final Logger logger = LogManager.getLogger()

    private String nodePath
    private ZooKeeper zooKeeper
    private byte[] bytes = new byte[0]
    private List<ACL> acls = [new ACL(ZooDefs.Perms.ALL, ZooDefs.Ids.ANYONE_ID_UNSAFE)]

    private AtomicBoolean locked = new AtomicBoolean(false)

    @Override
    void execute(Closure execute) {
        if (locked.get()) {
            execute.call()
        } else {
            this.zooKeeper.create(nodePath, bytes, acls, CreateMode.EPHEMERAL, { int rc, String path, Object ctx, String name ->
                try {
                    KeeperException.Code code = KeeperException.Code.get(rc)
                    switch (code) {
                        case KeeperException.Code.OK:
                            locked.set(true)
                            logger.info("the service has got this lock: ${nodePath}, code: ${code}")
                            execute.call()
                            break
                        default:
                            logger.warn("cannot lock node: ${nodePath}, code: ${code}")
                            break
                    }
                } catch (e) {
                    logger.warn(e)
                }
            }, null)
        }
    }

    @Override
    void release() {
        try {
            this.zooKeeper.close()
        } catch (Exception e) {
            logger.warn(e)
        } finally {
            this.zooKeeper = null
            locked.set(false)
        }
    }

    static ZookeeperExecutor valueOf(String connection, String nodePath, int sessionTimeout = 2000) {
        ZookeeperExecutor lock = new ZookeeperExecutor()
        lock.zooKeeper = new ZooKeeper(connection, sessionTimeout, { WatchedEvent event ->
            logger.debug(event)
        })
        lock.nodePath = nodePath
        return lock
    }

}
