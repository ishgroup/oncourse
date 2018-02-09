package ish.oncourse.configuration

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.ZooDefs
import org.apache.zookeeper.ZooKeeper

class InitZKRootNode {

    public static final String WILLOW_NODE = '/willow'
    public static final String SESSIONS_NODE = '/willow/sessions'
    public static final String EDITOR_SESSIONS_NODE = '/willow/editorSessions'

    private String zkHostPort

    private Logger logger = LogManager.logger

    private InitZKRootNode() {}
    
    static InitZKRootNode valueOf(String zkHostPort) {
        InitZKRootNode initializer = new InitZKRootNode()
        initializer.zkHostPort = zkHostPort
        
        initializer
    } 

    void init() {
        ZooKeeper keeper = new ZooKeeper(zkHostPort, 20000, {e -> logger.info(e)})
        if (keeper.exists(WILLOW_NODE, false) == null) {
            keeper.create(WILLOW_NODE, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)
        }

        if (keeper.exists(SESSIONS_NODE, false) == null) {
            keeper.create(SESSIONS_NODE, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)
        }

        if (keeper.exists(EDITOR_SESSIONS_NODE, false) == null) {
            keeper.create(EDITOR_SESSIONS_NODE, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)
        }
        keeper.close()
    }
}
