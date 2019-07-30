package ish.oncourse.services.lifecycle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.cayenne.DataChannelSyncFilter;
import org.apache.cayenne.DataChannelSyncFilterChain;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.graph.GraphChangeHandler;
import org.apache.cayenne.graph.GraphDiff;

/**
 * Cayenne Sync filter that captures and publishes all property changes before commit.
 *
 * @author n.timofeev
 */
public class PropertyChangeFilter implements DataChannelSyncFilter {

    private static final ThreadLocal<Map<ObjectId, Map<String, PropertyChange>>> PRE_COMMIT_CHANGES = new ThreadLocal<>();

    /**
     * @param persistent object we need changes for
     * @return changes or empty map
     */
    public static Map<String, PropertyChange> getChangesForObject(Persistent persistent) {
        Map<ObjectId, Map<String, PropertyChange>> changeMap = PRE_COMMIT_CHANGES.get();
        if(changeMap == null) {
            return Collections.emptyMap();
        }
        return changeMap.getOrDefault(persistent.getObjectId(), Collections.emptyMap());
    }

    @Override
    public GraphDiff onSync(ObjectContext originatingContext, GraphDiff changes, int syncType, DataChannelSyncFilterChain filterChain) {
        PRE_COMMIT_CHANGES.set(new HashMap<>());
        try {
            changes.apply(new PropertyChangeHandler());
            return filterChain.onSync(originatingContext, changes, syncType);
        } finally {
            PRE_COMMIT_CHANGES.set(null);
        }
    }

    public static class PropertyChange {
        private final Object oldValue;
        private final Object newValue;

        public PropertyChange(Object oldValue, Object newValue) {
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public Object getOldValue() {
            return oldValue;
        }

        public Object getNewValue() {
            return newValue;
        }
    }

    private static class PropertyChangeHandler implements GraphChangeHandler {

        @Override
        public void nodePropertyChanged(Object nodeId, String property, Object oldValue, Object newValue) {
            Map<ObjectId, Map<String, PropertyChange>> changeMap = PRE_COMMIT_CHANGES.get();
            if(changeMap == null) {
                return;
            }
            changeMap.computeIfAbsent((ObjectId)nodeId, id -> new HashMap<>())
                    .put(property, new PropertyChange(oldValue, newValue));
        }

        @Override
        public void nodeIdChanged(Object nodeId, Object newId) {
        }

        @Override
        public void nodeCreated(Object nodeId) {
        }

        @Override
        public void nodeRemoved(Object nodeId) {
        }

        @Override
        public void arcCreated(Object nodeId, Object targetNodeId, Object arcId) {
        }

        @Override
        public void arcDeleted(Object nodeId, Object targetNodeId, Object arcId) {
        }
    }
}
