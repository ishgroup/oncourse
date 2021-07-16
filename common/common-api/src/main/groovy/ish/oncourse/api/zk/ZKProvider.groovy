package ish.oncourse.api.zk

import com.google.inject.Inject
import ish.oncourse.configuration.Configuration
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.Watcher
import org.apache.zookeeper.ZooKeeper

import static ish.oncourse.configuration.Configuration.AppProperty.ZK_HOST
import static org.apache.zookeeper.Watcher.Event.KeeperState.Disconnected
import static org.apache.zookeeper.Watcher.Event.KeeperState.Expired

class ZKProvider {
    
    private static final int zkClientTimeOut = 20000
    
    private String zkHost
    private Map<String, ZooKeeper> zkMap = [:]
    
    private Logger logger = LogManager.logger
    
    @Inject
    ZKProvider() {
        zkHost = Configuration.getValue(ZK_HOST)
        if (!zkHost) {
            throw new IllegalStateException('Zookeeper host property undefined')
        }
    }
    
    synchronized ZooKeeper getZk(String rootNode) throws InterruptedException {
        return  zkMap.computeIfAbsent(rootNode, {createZk(rootNode)})
    }

    private ZooKeeper createZk(String rootNode){
        try {
            ZooKeeper zk =  new ZooKeeper(String.format("%s%s", zkHost, rootNode), zkClientTimeOut, new Watcher() {
                @Override
                void process(WatchedEvent event) {
                    switch (event.state) {
                        case Expired:
                        case Disconnected:
                            zkMap.remove(rootNode)?.close()
                            break
                        default:
                            break
                    }
                }
            })
            return zk
        } catch (IOException e) {
            logger.catching(e)
            throw new RuntimeException(e)
        }
    }
}
