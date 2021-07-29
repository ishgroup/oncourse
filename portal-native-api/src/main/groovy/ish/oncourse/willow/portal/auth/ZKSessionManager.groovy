package ish.oncourse.willow.portal.auth

import ish.oncourse.api.zk.AbstractZKSessionManager
import ish.oncourse.configuration.InitZKRootNode
import ish.oncourse.model.User

class ZKSessionManager  extends AbstractZKSessionManager {

    @Override
    String getRootNode() {
        InitZKRootNode.PORTAL_SESSIONS_NODE
    }
    
    void removeSessions(User user) {
        deleteAll("/$User.simpleName-$user.id")
    }
}
