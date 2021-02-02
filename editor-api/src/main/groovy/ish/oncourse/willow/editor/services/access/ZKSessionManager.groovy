package ish.oncourse.willow.editor.services.access

import ish.oncourse.api.zk.AbstractZKSessionManager
import ish.oncourse.configuration.InitZKRootNode 

class ZKSessionManager extends AbstractZKSessionManager {

    @Override
    String getRootNode() {
        return InitZKRootNode.EDITOR_SESSIONS_NODE
    }
}
