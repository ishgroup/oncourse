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

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.server.api.dao.DocumentDao
import ish.oncourse.server.api.v1.function.DocumentFunctions
import ish.oncourse.server.api.v1.model.DocumentDTO
import ish.oncourse.server.cayenne.AttachableTrait
import ish.oncourse.server.cayenne.AttachmentRelation
import ish.oncourse.server.cayenne.Document
import ish.oncourse.server.cayenne.DocumentTagRelation
import ish.oncourse.server.document.DocumentService
import ish.util.EntityUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById

import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocument
import static ish.oncourse.server.api.v1.function.TagFunctions.updateTags
import static org.apache.commons.lang3.StringUtils.*

@CompileStatic
class DocumentApiService extends TaggableApiService<DocumentDTO, Document, DocumentDao> {

    @Inject
    private DocumentService documentService


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

        updateTags(cayenneModel, cayenneModel.taggingRelations, restModel.tags, DocumentTagRelation, cayenneModel.context)
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

    void makeDocumentInactive(Long docId, ObjectContext context) {
        makeDocumentInactive(getEntityAndValidateExistence(context, docId))
    }

    static void makeDocumentInactive(Document document) {
        document.isRemoved = Boolean.TRUE
    }

    Closure getAction (String key, String value) {
        Closure action = super.getAction(key, value)
        if (!action) {
            switch (key) {
                case Document.IS_REMOVED.name:
                    action = { Document d ->
                        d.isRemoved = Boolean.valueOf(value)
                    }
                    break
                default:
                    validator.throwClientErrorException(key, "Unsupported attribute")
            }
        }
        action
    }


    List<DocumentDTO> getDocumentsBy(String entityName, Long id) {
        AttachableTrait attachableTrait = validateAttachable(entityName, id)
        (attachableTrait.attachmentRelations as List<AttachmentRelation>)
                .findAll {!it.document.isRemoved}
                .collect {toRestDocument(it.document, it.documentVersion?.id, documentService) }
    }


    AttachableTrait validateAttachable(String entityName, Long entityId) {

        if (entityId == null) {
            validator.throwClientErrorException('entityId', 'Related object id is required')
        }
        if (entityName == null) {
            validator.throwClientErrorException('entityName', 'Related object name is required')
        }

        def entityClass = EntityUtil.entityClassForName(entityName)
        if(!(entityClass instanceof Class<? extends AttachableTrait>)){
            validator.throwClientErrorException('entityName', 'Related object name is wrong')
        }

        def context = cayenneService.newContext
        AttachableTrait attachableTrait = SelectById.query(entityClass as Class<? extends AttachableTrait>, entityId).selectOne(context)
        if (attachableTrait == null) {
            validator.throwClientErrorException('entityId', 'Related object doesn\'t exist')
        }
        return attachableTrait

    }

    void updateDocumentsAttachedTo(String entityName, Long entityId, List<Long> documentIds){
        def attachable = validateAttachable(entityName, entityId)
        DocumentFunctions.updateDocuments(attachable, documentIds, attachable.context)
    }
}
