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

package ish.oncourse.server.cayenne

import ish.oncourse.API
import org.apache.cayenne.ObjectContext

/**
 * Entities which extends this trait may have custom fields, for example Contact, Course
 */
@API
trait AttachableTrait {

    abstract Long getId()

    abstract String getEntityName()

    abstract void addToAttachmentRelations(AttachmentRelation relation)

    abstract void removeFromAttachmentRelations(AttachmentRelation relation)

    abstract List<? extends AttachmentRelation> getAttachmentRelations()

    abstract Class<? extends AttachmentRelation> getRelationClass()

    abstract ObjectContext getContext()

    /**
     * @return all attached documents
     */
    @API
    List<Document> getDocuments() {
        return attachmentRelations*.document
    }

    @Deprecated
    void attacheDocument(Document doc) {
        attachDocument(doc)
    }

    /**
     * @param doc the document to be attached to this tutor
     *
     */
    @API
    void attachDocument(Document doc) {
        AttachmentRelation relation = context.newObject(relationClass)
        relation.setDocument(doc)
        addToAttachmentRelations(relation)
    }


    List<? extends AttachmentRelation> getActiveAttachments() {
        return attachmentRelations.findAll { !(it as AttachmentRelation).document.isRemoved }
    }

}
