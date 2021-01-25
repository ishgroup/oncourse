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

package ish.oncourse.server.api.service

import groovy.transform.CompileStatic
import ish.oncourse.server.api.dao.DocumentDao
import static ish.oncourse.server.api.v1.function.TagFunctions.updateTags
import ish.oncourse.server.api.v1.model.DocumentDTO
import ish.oncourse.server.cayenne.Document
import ish.oncourse.server.cayenne.DocumentTagRelation
import org.apache.cayenne.ObjectContext
import static org.apache.commons.lang3.StringUtils.isBlank
import static org.apache.commons.lang3.StringUtils.trimToEmpty
import static org.apache.commons.lang3.StringUtils.trimToNull

@CompileStatic
class DocumentApiService extends TaggableApiService<DocumentDTO, Document, DocumentDao> {

    @Override
    Class<Document> getPersistentClass() {
        return Document
    }

    @Override
    DocumentDTO toRestModel(Document cayenneModel) {
        // Is not applicable for this entity
        return null
    }

    @Override
    Document toCayenneModel(DocumentDTO restModel, Document cayenneModel) {
        cayenneModel.name = trimToNull(restModel.name)
        cayenneModel.isRemoved = restModel.removed
        cayenneModel.isShared = restModel.shared
        cayenneModel.description = trimToNull(restModel.description)
        cayenneModel.webVisibility = restModel.access.dbType

        updateTags(cayenneModel, cayenneModel.taggingRelations, restModel.tags*.id, DocumentTagRelation, cayenneModel.context)
        cayenneModel
    }

    @Override
    void validateModelBeforeSave(DocumentDTO restModel, ObjectContext context, Long id) {

        if (isBlank(restModel.name)) {
            validator.throwClientErrorException(id, 'name', 'Name is required.')
        } else if (trimToEmpty(restModel.name).size() > 512) {
            validator.throwClientErrorException(id, 'name', 'Name cannot be more than 512 chars.')
        }

        if (trimToEmpty(restModel.description).size() > 32000) {
            validator.throwClientErrorException(id, 'description', 'Description cannot be more than 32000 chars.')
        }

        if (restModel.access == null) {
            validator.throwClientErrorException(id, 'access', 'Security level is required')
        }

        if (restModel.shared == null) {
            validator.throwClientErrorException(id, 'shared', 'Shared flag is required')
        }

    }

    @Override
    void validateModelBeforeRemove(Document cayenneModel) {
        // Is not applicable for this entity
    }

    void changeDocumentVisibility(Long docId, ObjectContext context) {
        changeDocumentVisibility(getEntityAndValidateExistence(context, docId))
    }

    static void changeDocumentVisibility(Document document) {
        document.isRemoved = document.isRemoved ? Boolean.FALSE : Boolean.TRUE
    }

    Closure getAction (String key, String value) {
        Closure action = super.getAction(key, value)
        if (!action) {
            validator.throwClientErrorException(key, "Unsupported attribute")
        }
        action
    }
}
