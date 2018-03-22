package ish.oncourse.willow.editor.services.access

import com.google.inject.Inject
import ish.oncourse.configuration.InitZKRootNode
import ish.oncourse.willow.editor.services.ZKProvider
import org.apache.commons.lang3.SerializationUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.KeeperException
import org.apache.zookeeper.ZooDefs
import org.apache.zookeeper.ZooKeeper

import java.time.LocalDateTime


class ZKSessionManager {
    
    private Logger logger = LogManager.logger
    private ZKProvider provider

    @Inject
    ZKSessionManager(ZKProvider provider) {
        this.provider = provider
    }

    void persistSession(String userId,  String sessionId) {
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
    
    private ZooKeeper getZk() {
        return provider.getZk(InitZKRootNode.EDITOR_SESSIONS_NODE)
    }


}
