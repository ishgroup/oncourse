/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.oncourse.server.lifecycle;

import org.apache.cayenne.DataChannelSyncFilter;
import org.apache.cayenne.DataChannelSyncFilterChain;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.graph.GraphDiff;

import java.util.HashMap;
import java.util.Map;

public class ChangeFilter implements DataChannelSyncFilter {

	private static final ThreadLocal<Map<ObjectContext,GraphDiff>> PRE_COMMIT_GRAPH_DIFF = new ThreadLocal<>();

	public static GraphDiff preCommitGraphDiff(ObjectContext objectContext) {
		return PRE_COMMIT_GRAPH_DIFF.get().get(objectContext);
	}

	public GraphDiff onSync(
			ObjectContext originatingContext,
			GraphDiff changes,
			int syncType,
			DataChannelSyncFilterChain filterChain) {
		try {
		    if (PRE_COMMIT_GRAPH_DIFF.get() == null) {
                PRE_COMMIT_GRAPH_DIFF.set(new HashMap<>());
            }
            PRE_COMMIT_GRAPH_DIFF.get().put(originatingContext, changes);
			return filterChain.onSync(originatingContext, changes, syncType);
		} finally {
			PRE_COMMIT_GRAPH_DIFF.get().put(originatingContext, null);
		}
	}

	public static PropertyChange getAtrAttributeChange(ObjectContext objectContext, ObjectId objectId, String attribute) {
		var diff = preCommitGraphDiff(objectContext);
		if (diff != null) {
			var changes = new GraphDiffParser(diff).getChanges(objectId);
			if (changes != null) {
				return changes.get(attribute);
			}
		}
		return null;
	}

}
