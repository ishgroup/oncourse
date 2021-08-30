package ish.oncourse.willow.portal.auth

import ish.oncourse.api.zk.AbstractZKSessionManager
import ish.oncourse.configuration.InitZKRootNode
import ish.oncourse.model.User
import org.apache.zookeeper.CreateMode

class ZKSessionManager extends AbstractZKSessionManager {

    @Override
    String getRootNode() {
        InitZKRootNode.PORTAL_SESSIONS_NODE
    }
    
    void removeSessions(User user) {
        deleteAll("${getUserId(user)}")
    }

    String persistSession(User user, String sessionId, CreateMode mode) {
        String userId = getUserId(user)
        persistSession(getUserId(user), sessionId, mode)
        return getSessionToken(userId, sessionId)
        
    }
    
    String getSessionToken(User user) {
        String userId = getUserId(user)
        List<String> children = getChildren(userId)
        if (children && children.size() == 1) {
            return getSessionToken(userId, children[0])
        } else {
            deleteAll(userId)
            return null
        }
    }

    static String getSessionToken(String userId, String sessionId ) {
        return  "$userId&${sessionId}"
    }
    
    static String getUserId(User user) {
        return  "$User.simpleName-$user.id"
    }
}
