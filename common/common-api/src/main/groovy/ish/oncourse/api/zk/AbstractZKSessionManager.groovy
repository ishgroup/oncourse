package ish.oncourse.api.zk

import com.google.inject.Inject
import org.apache.commons.lang3.SerializationUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.KeeperException
import org.apache.zookeeper.ZooDefs
import org.apache.zookeeper.ZooKeeper

import java.time.LocalDateTime 

abstract class AbstractZKSessionManager {

    private Logger logger = LogManager.logger
    private ZKProvider provider

    @Inject
    ZKSessionManager(ZKProvider provider) {
        this.provider = provider
    }

    void persistSession(String userId, String sessionId, CreateMode mode = CreateMode.PERSISTENT) {
        try {
            if (zk.exists("/$userId", false) == null) {
                zk.create("/$userId", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)
            }
            zk.create("/$userId/$sessionId", SerializationUtils.serialize(LocalDateTime.now()), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)
        } catch (KeeperException | InterruptedException e) {
            logger.catching(e)
            throw new RuntimeException(e)
        }
    }

    boolean sessionExist(String sessionPath) {
        zk.exists(sessionPath, false)
    }

    protected void deleteAll(String root) throws KeeperException, InterruptedException { 
       zk.getChildren(root, false)?.each {
           deleteAll("$root/$it")
       }
       zk.delete(root, -1);
    }
    
    private ZooKeeper getZk() {
        return provider.getZk(getRootNode())
    }

    abstract String getRootNode()
}
