package ish.oncourse.services.persistence;

import java.util.HashMap;
import java.util.Map;

import org.apache.cayenne.DataChannel;
import org.apache.cayenne.DataChannelFilter;
import org.apache.cayenne.DataChannelFilterChain;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.QueryResponse;
import org.apache.cayenne.graph.GraphDiff;
import org.apache.cayenne.lifecycle.changeset.ChangeSet;
import org.apache.cayenne.lifecycle.changeset.GenericChangeSet;
import org.apache.cayenne.query.Query;

public class WillowChangeSetFilter implements DataChannelFilter {
	
	private static final ThreadLocal<Map<ObjectContext, ChangeSet>> PRE_COMMIT_CHANGE_SET = new ThreadLocal<Map<ObjectContext, ChangeSet>>();

    public static ChangeSet preCommitChangeSet(ObjectContext ctx) {
        return PRE_COMMIT_CHANGE_SET.get().get(ctx);
    }

    public void init(DataChannel channel) {
    	PRE_COMMIT_CHANGE_SET.set(new HashMap<ObjectContext, ChangeSet>());
    }

    public QueryResponse onQuery(
            ObjectContext originatingContext,
            Query query,
            DataChannelFilterChain filterChain) {
        return filterChain.onQuery(originatingContext, query);
    }

    public GraphDiff onSync(
            ObjectContext originatingContext,
            GraphDiff changes,
            int syncType,
            DataChannelFilterChain filterChain) {

        try {
            PRE_COMMIT_CHANGE_SET.get().put(originatingContext, new GenericChangeSet(changes));
            return filterChain.onSync(originatingContext, changes, syncType);
        }
        finally {
            PRE_COMMIT_CHANGE_SET.get().remove(originatingContext);
        }
    }
}
