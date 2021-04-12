package ish.oncourse.website.cache

import ish.oncourse.cayenne.cache.ICacheInvalidationService
import ish.oncourse.configuration.Configuration
import org.apache.cayenne.cache.QueryCache
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.zookeeper.KeeperException
import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.Watcher
import org.apache.zookeeper.ZooKeeper

import static ish.oncourse.configuration.Configuration.AppProperty.ZK_HOST
import static ish.oncourse.configuration.InitZKRootNode.WEB_PUBLISH
import static org.apache.zookeeper.Watcher.Event.EventType.NodeDataChanged
import static org.apache.zookeeper.Watcher.Event.KeeperState.Disconnected
import static org.apache.zookeeper.Watcher.Event.KeeperState.Expired 

class CacheInvalidationService implements ICacheInvalidationService,Watcher {

    private static final Logger logger = LogManager.getLogger();
    private QueryCache cache
    private ZooKeeper zk
    
    

    @Override
    void process(WatchedEvent event) {
        switch (event.state) {
            case Disconnected:
            case Expired:
                init()
                return
            default:
                break
        }
        
        if (NodeDataChanged == event.type) {
            try {
                String siteKey = new String(zk.getData(WEB_PUBLISH, this, null))
                logger.warn("Invalidate query cache for $siteKey")
                cache.removeGroup(siteKey)  
            } catch (KeeperException e) {
                logger.error("Fail to invalidate query cache: $event")
                logger.catching(e)
            }
        }
    }

    @Override
    void setCache(QueryCache cache) {
        this.cache = cache
    }
    
    
    @Override
    void init() {
        if (cache == null) {
            throw  new IllegalStateException("Query cache is not set")
        }
        if (zk != null) {
            try {
                zk.close()
            } catch (Exception ignore) {}
        }
        zk = new ZooKeeper(Configuration.getValue(ZK_HOST), 2000, null)
        zk.getData(WEB_PUBLISH, this, null)
    }

    
}
