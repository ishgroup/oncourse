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

import ish.oncourse.server.cayenne.Tag;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.graph.GraphChangeHandler;

import java.util.HashMap;
import java.util.Map;

public class TagNameChangeHandler implements GraphChangeHandler {

    private Map<ObjectId, TagNameChange> changes = new HashMap<>();


    public TagNameChangeHandler(ObjectContext context) {
    }

    @Override
    public void nodeIdChanged(Object nodeId, Object newId) {}

    @Override
    public void nodeCreated(Object nodeId) {}

    @Override
    public void nodeRemoved(Object nodeId) {}

    @Override
    public void nodePropertyChanged(Object nodeId, String property, Object oldValue, Object newValue) {
        if (Tag.NAME.getName().equals(property)) {
            changes.put((ObjectId) nodeId, new TagNameChange((String) oldValue, (String) newValue));
        }
    }

    @Override
    public void arcCreated(Object nodeId, Object targetNodeId, Object arcId) {}

    @Override
    public void arcDeleted(Object nodeId, Object targetNodeId, Object arcId) {}

    public String getOldValueFor(ObjectId tagId) {
        return changes.get(tagId) != null ? changes.get(tagId).getOldValue() : null;
    }

    public String getNewValueFor(ObjectId tagId) {
        return changes.get(tagId) != null ? changes.get(tagId).getNewValue() : null;
    }

    public static class TagNameChange {

        private String oldValue;
        private String newValue;

        public TagNameChange(String oldValue, String newValue) {
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public String getOldValue() {
            return oldValue;
        }

        public void setOldValue(String oldValue) {
            this.oldValue = oldValue;
        }

        public String getNewValue() {
            return newValue;
        }

        public void setNewValue(String newValue) {
            this.newValue = newValue;
        }
    }
}
