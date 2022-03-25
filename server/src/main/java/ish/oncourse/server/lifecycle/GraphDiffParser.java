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

import org.apache.cayenne.ObjectId;
import org.apache.cayenne.graph.GraphChangeHandler;
import org.apache.cayenne.graph.GraphDiff;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GraphDiffParser {

    public static final String OBJECT_ID_PROPERTY_NAME = "cayenne:objectId";

    private GraphDiff diff;
    private Map<ObjectId, Map<String, PropertyChange>> changes;

    public GraphDiffParser(GraphDiff diff) {
        this.diff = diff;
    }

    public Map<String, PropertyChange> getChanges(ObjectId objectId) {
        var changes = getChanges().get(objectId);
        return changes != null ? changes : Collections.emptyMap();
    }

    private Map<ObjectId, Map<String, PropertyChange>> getChanges() {
        if (changes == null) {
            changes = parseDiff();
        }

        return changes;
    }

    private Map<ObjectId, Map<String, PropertyChange>> parseDiff() {

        final Map<ObjectId, Map<String, PropertyChange>> changes = new HashMap<>();

        diff.apply(new GraphChangeHandler() {

            private Map<String, PropertyChange> getChangeMap(Object id) {
                var map = changes.get(id);

                if (map == null) {
                    map = new HashMap<>();
                    changes.put((ObjectId) id, map);
                }

                return map;
            }

            PropertyChange getChange(Object id, String property, Object oldValue) {
                var map = getChangeMap(id);

                var change = map.get(property);
                if (change == null) {
                    change = new PropertyChange(property, oldValue);
                    map.put(property, change);
                }

                return change;
            }

            public void nodeRemoved(Object nodeId) {
                // noop, don't care, we'll still track the changes for deleted objects.
            }

            public void nodeCreated(Object nodeId) {
                // noop (??)
            }

            public void arcDeleted(Object nodeId, Object targetNodeId, Object arcId) {
                // record the fact of relationship change... TODO: analyze relationship
                // semantics and record changset values
                getChange(nodeId, arcId.toString(), null);
            }

            public void arcCreated(Object nodeId, Object targetNodeId, Object arcId) {
                // record the fact of relationship change... TODO: analyze relationship
                // semantics and record changset values
                getChange(nodeId, arcId.toString(), null);
            }

            public void nodePropertyChanged(
                    Object nodeId,
                    String property,
                    Object oldValue,
                    Object newValue) {
                getChange(nodeId, property, oldValue).setNewValue(newValue);
            }

            public void nodeIdChanged(Object nodeId, Object newId) {

                // store the same change set under old and new ids to allow lookup before
                // and after the commit
                var map = getChangeMap(nodeId);
                changes.put((ObjectId) newId, map);

                // record a change for a special ID "property"
                getChange(nodeId, OBJECT_ID_PROPERTY_NAME, nodeId).setNewValue(newId);
            }

        });

        return changes;
    }
}
