package org.apache.cayenne.access

import org.apache.cayenne.DataChannelSyncFilter
import org.apache.cayenne.DataChannelSyncFilterChain
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.graph.GraphDiff

import static ish.oncourse.willow.filters.XValidateFilter.ThreadLocalValidateOnly

class ValidationFilter implements DataChannelSyncFilter {

    @Override
    GraphDiff onSync(ObjectContext originatingContext, GraphDiff changes, int syncType, DataChannelSyncFilterChain filterChain) {
        if (ThreadLocalValidateOnly.get()) {
            ((ObjectStoreGraphDiff) changes).validateAndCheckNoop()
            return changes
        } else {
            return filterChain.onSync(originatingContext, changes, syncType)
        }
    }
}
