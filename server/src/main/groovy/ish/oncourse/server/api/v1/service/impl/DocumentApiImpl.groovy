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

package ish.oncourse.server.api.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.document.DocumentService
import ish.s3.AmazonS3Service

import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.function.EntityFunctions.checkForBadRequest
import ish.oncourse.server.api.service.DocumentApiService
import static ish.oncourse.server.api.v1.function.DocumentFunctions.createDocument
import static ish.oncourse.server.api.v1.function.DocumentFunctions.createDocumentVersion
import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocument
import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocumentVersion
import static ish.oncourse.server.api.v1.function.DocumentFunctions.validateForSave
import ish.oncourse.server.api.v1.model.DiffDTO
import ish.oncourse.server.api.v1.model.DocumentDTO
import ish.oncourse.server.api.v1.model.DocumentVersionDTO
import ish.oncourse.server.api.v1.model.DocumentVisibilityDTO
import ish.oncourse.server.api.v1.service.DocumentApi
import ish.oncourse.server.cayenne.Document
import ish.oncourse.server.cayenne.DocumentVersion
import ish.oncourse.server.users.SystemUserService
import ish.s3.S3Service
import ish.util.SecurityUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static ish.oncourse.server.api.v1.function.DocumentFunctions.validateStoragePlace

class DocumentApiImpl implements DocumentApi {

    private static Logger logger = LogManager.logger

    @Inject
    private ICayenneService cayenneService

    @Inject
    DocumentService documentService

    @Inject
    SystemUserService systemUserService

    @Inject
    DocumentApiService service

    @Override
    DocumentDTO create(String name, String description, Boolean shared, String access, String fileName, File content, String tags) {
        ObjectContext context = cayenneService.newContext

        DocumentVisibilityDTO visibility = DocumentVisibilityDTO.fromValue(access)
        List<Long> tagIds = tags ? tags.split(',').collect {Long.valueOf(it)} : []

        checkForBadRequest(validateForSave(content.getBytes(), fileName, name,  visibility, tagIds, shared, context))
        checkForBadRequest(validateStoragePlace(content.getBytes(), documentService, context))

        Document dbDocument = createDocument(name, description, visibility, tagIds, shared,  context)
        dbDocument.fileUUID = UUID.randomUUID().toString()
        DocumentVersion version = createDocumentVersion(dbDocument, content.getBytes(), fileName, context, documentService, systemUserService.currentUser)
        context.commitChanges()

        return toRestDocument(dbDocument, version.id, documentService)
    }

    @Override
    DocumentVersionDTO createVersion(Long id, String fileName, File content) {
        ObjectContext context = cayenneService.newContext
        checkForBadRequest(validateStoragePlace(content.getBytes(), documentService, context))
        Document document = getRecordById(context, Document, id)
        AmazonS3Service s3Service = null
        if (documentService.usingExternalStorage) {
            s3Service = new AmazonS3Service(documentService)
        }
        DocumentVersion version = createDocumentVersion(document, content.getBytes(), fileName, context, documentService, systemUserService.currentUser)
        context.commitChanges()
        return toRestDocumentVersion(version, s3Service)
    }

    @Override
    DocumentDTO get(Long id) {
        return toRestDocument(getRecordById(cayenneService.newContext, Document, id), null, documentService)
    }

    @Override
    DocumentDTO search(byte[] content) {
        ObjectContext context = cayenneService.newContext
        if (content && content.length) {
            String hash = SecurityUtil.hashByteArray(content)

            Document document = ObjectSelect.query(Document)
                    .where(Document.VERSIONS.dot(DocumentVersion.HASH).eq(hash))
                    .selectFirst(context)
            if (document) {
                return toRestDocument(document, null, documentService)
            }
        }
        return null
    }

    @Override
    void remove(Long id) {
        ObjectContext context = cayenneService.newContext
        service.changeDocumentVisibility(id, context)
        context.commitChanges()
    }

    @Override
    void update(Long id, DocumentDTO documentDto) {
        ObjectContext context = cayenneService.newContext
        Document dbDocument = service.getEntityAndValidateExistence(context, id)
        service.validateModelBeforeSave(documentDto, context, id)
        service.toCayenneModel(documentDto, dbDocument)
        if (documentService.usingExternalStorage && dbDocument.fileUUID != null) {
            AmazonS3Service s3Service = new AmazonS3Service(documentService)
            String uuid = dbDocument.getFileUUID()
            dbDocument.versions.each { version ->
                try {
                    s3Service.changeVisibility(uuid, version.versionId, dbDocument.webVisibility)
                } catch (Exception e) {
                    logger.error("Could not change document visibility uuid: {}, versionId: {}", dbDocument.getId(), version.versionId, e)
                }
            }
        }
        dbDocument.getContext().commitChanges()
    }

    @Override
    void bulkChange(DiffDTO diff) {
        service.bulkChange(diff)
    }

}
