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
import ish.oncourse.server.api.v1.model.DocumentVisibilityDTO
import ish.oncourse.server.cayenne.DocumentVersion
import ish.oncourse.server.document.DocumentService
import ish.s3.AmazonS3Service
import ish.util.LocalDateUtils

import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocumentAttachmentRelations
import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocumentVersion
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

    @Inject
    private DocumentService documentService

    @Override
    Class<Document> getPersistentClass() {
        return Document
    }

    @Override
    DocumentDTO toRestModel(Document dbDocument) {
        new DocumentDTO().with { document ->
            document.id = dbDocument.id
            document.name = dbDocument.name
            document.added = LocalDateUtils.dateToTimeValue(dbDocument.added)
            document.tags = dbDocument.allTags.collect { it.id }

            document.thumbnail = dbDocument.currentVersion?.thumbnail
            AmazonS3Service s3Service
            if (documentService.usingExternalStorage) {
                s3Service = new AmazonS3Service(documentService)
            }
            document.versions = dbDocument.versions.collect { toRestDocumentVersion(it, s3Service) }.sort { it.added }.reverse()

            document.description = dbDocument.description
            document.access = DocumentVisibilityDTO.values()[0].fromDbType(dbDocument.webVisibility)
            document.displayAccess = dbDocument.displayWebVisibility
            document.shared = dbDocument.isShared
            document.removed = dbDocument.isRemoved
            document.createdOn = LocalDateUtils.dateToTimeValue(dbDocument.createdOn)
            document.modifiedOn = LocalDateUtils.dateToTimeValue(dbDocument.modifiedOn)
            document.attachmentRelations = toRestDocumentAttachmentRelations(dbDocument.attachmentRelations)
            if (s3Service) {
                document.urlWithoutVersionId = s3Service.getFileUrl(dbDocument.fileUUID, null, dbDocument.webVisibility)
            }
            document
        }
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
            validator.throwClientErrorException(id, 'access', 'Security level is required.')
        }

        if (restModel.shared == null) {
            validator.throwClientErrorException(id, 'shared', 'Shared flag is required.')
        }

        if (restModel.versions.size() == 0) {
            validator.throwClientErrorException(id, 'versions', 'At least one document version is required.')
        }

        if (restModel.versions.findAll {it.current}.size() == 0) {
            validator.throwClientErrorException(id, 'versions', 'At least one document version must be current.')
        }

        if (restModel.versions.findAll {it.current}.size() > 1) {
            validator.throwClientErrorException(id, 'versions', 'Document cannot contain 2 current versions.')
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
}
