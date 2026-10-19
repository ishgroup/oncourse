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

import java.util.IdentityHashMap;
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
		Map<ObjectContext, GraphDiff> map = PRE_COMMIT_GRAPH_DIFF.get();
		boolean isOuterCall = (map == null);
		if (isOuterCall) {
			map = new IdentityHashMap<>();
			PRE_COMMIT_GRAPH_DIFF.set(map);
		}
		GraphDiff previous = map.get(originatingContext);
		map.put(originatingContext, changes);
		try {
			return filterChain.onSync(originatingContext, changes, syncType);
		} finally {
			map.put(originatingContext, previous);
			if (isOuterCall) {
				PRE_COMMIT_GRAPH_DIFF.remove();
			}
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
