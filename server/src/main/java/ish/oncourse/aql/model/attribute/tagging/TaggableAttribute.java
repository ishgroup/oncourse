/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.aql.model.attribute.tagging;

import ish.common.types.NodeType;

import java.util.HashMap;
import java.util.Map;

public enum TaggableAttribute {
    TAGS("tags", "taggingRelations+.tag", NodeType.TAG),
    CHECKED_TASKS("checkedTasks", "taggingRelations+.tag", NodeType.CHECKLIST),
    UNCHECKED_TASKS("uncheckedTasks", "taggingRelations+.tag", NodeType.CHECKLIST),
    COMPLETED_CHECKLISTS("completedChecklists", "taggingRelations+.tag+.parentTag", NodeType.CHECKLIST),
    UNCOMPLETED_CHECKLISTS("notCompletedChecklists", "taggingRelations+.tag+.parentTag", NodeType.CHECKLIST);

    private String currentAlias;
    private String realPath;
    private NodeType nodeType;

    TaggableAttribute(String currentAlias, String realPath, NodeType nodeType) {
        this.currentAlias = currentAlias;
        this.realPath = realPath;
        this.nodeType = nodeType;
    }

    public String getCurrentAlias() {
        return currentAlias;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public String getRealPath() {
        return realPath;
    }

    public Map<String,String> getPathAliases(){
        return new HashMap<>(){{
            put(currentAlias, realPath);
        }};
    }

    @Override
    public String toString() {
        return currentAlias;
    }
}

