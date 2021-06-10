package ish.oncourse.willow.billing.filter

import ish.oncourse.api.zk.AbstractZKSessionManager
import ish.oncourse.configuration.InitZKRootNode

class ZKSessionManager extends AbstractZKSessionManager {
    
    @Override
    String getRootNode() {
        InitZKRootNode.BILLING_SESSIONS_NODE
    }
}
