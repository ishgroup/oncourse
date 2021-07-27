package ish.oncourse.willow.portal.auth

import ish.oncourse.api.zk.AbstractZKSessionManager
import ish.oncourse.configuration.InitZKRootNode

class ZKSessionManager  extends AbstractZKSessionManager {

    @Override
    String getRootNode() {
        InitZKRootNode.PORTAL_SESSIONS_NODE
    }
}
