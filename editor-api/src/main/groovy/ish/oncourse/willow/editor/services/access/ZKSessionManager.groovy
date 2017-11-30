package ish.oncourse.willow.editor.services.access

import com.google.inject.Inject
import ish.oncourse.configuration.Configuration
import ish.oncourse.configuration.InitZKRootNode
import org.apache.commons.lang3.SerializationUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.KeeperException
import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.Watcher
import org.apache.zookeeper.ZooDefs
import org.apache.zookeeper.ZooKeeper

import java.time.LocalDateTime

import static ish.oncourse.configuration.Configuration.AppProperty.ZK_HOST
import static org.apache.zookeeper.Watcher.Event.KeeperState.Disconnected
import static org.apache.zookeeper.Watcher.Event.KeeperState.Expired

class ZKSessionManager {
    
    private Logger logger = LogManager.logger

    private static final int zkClientTimeOut = 20000
    private ZooKeeper zooKeeper
    private boolean dead = false
    private String zkHost
    
    @Inject
    ZKSessionManager() {
        zkHost= Configuration.getValue(ZK_HOST)
        if (!zkHost) {
            throw new IllegalStateException('Zookeeper host property undefined')
        }
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

    private ZooKeeper getZk() throws InterruptedException {
        if (dead) {
            if (zooKeeper) {
                zooKeeper.close()
            }
            zooKeeper = createZk()
            dead = false
        }
        return zooKeeper
    }

    private ZooKeeper createZk(){
        try {
            return new ZooKeeper(String.format("%s%s", zkHost, InitZKRootNode.EDITOR_SESSIONS_NODE), zkClientTimeOut, new Watcher() {
                @Override
                void process(WatchedEvent event) {
                    switch (event.state) {
                        case Expired:
                        case Disconnected:
                            dead = true
                            break
                        default:
                            break
                    }
                }
            })
        } catch (IOException e) {
            logger.catching(e)
            throw new RuntimeException(e)
        }
    }
}
