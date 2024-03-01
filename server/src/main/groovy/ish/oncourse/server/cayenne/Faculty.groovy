/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import ish.common.types.NodeType
import ish.oncourse.API
import ish.oncourse.server.cayenne.glue._Faculty

import javax.annotation.Nonnull

class Faculty extends _Faculty implements NotableTrait, AttachableTrait {

    public static final int FACULTY_NAME_MAX_LENGTH = 200
    public static final int FACULTY_CODE_MAX_LENGTH = 32


    /**
     * @return The list of tags assigned to site
     */
    @Nonnull
    @API
    List<Tag> getTags() {
        List<Tag> tagList = new ArrayList<>(getTaggingRelations().size())
        for (FacultyTagRelation relation : getTaggingRelations()) {
            if(relation.tag?.nodeType?.equals(NodeType.TAG))
                tagList.add(relation.getTag())
        }
        return tagList
    }

    @Override
    Class<? extends TagRelation> getTagRelationClass() {
        return FacultyTagRelation.class
    }

    @Override
    void addToAttachmentRelations(AttachmentRelation relation) {
        addToAttachmentRelations((FacultyAttachmentRelation) relation)
    }

    @Override
    void removeFromAttachmentRelations(AttachmentRelation relation) {
        removeFromAttachmentRelations((FacultyAttachmentRelation) relation)
    }

    @Override
    Class<? extends AttachmentRelation> getRelationClass() {
        return FacultyAttachmentRelation.class
    }
}
