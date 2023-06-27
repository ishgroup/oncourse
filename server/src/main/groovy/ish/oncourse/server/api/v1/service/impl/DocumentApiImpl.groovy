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
import ish.oncourse.server.api.service.DocumentApiService
import ish.oncourse.server.api.v1.model.DiffDTO
import ish.oncourse.server.api.v1.model.DocumentDTO
import ish.oncourse.server.api.v1.model.DocumentVersionDTO
import ish.oncourse.server.api.v1.model.DocumentVisibilityDTO
import ish.oncourse.server.api.v1.service.DocumentApi
import ish.oncourse.server.cayenne.Document
import ish.oncourse.server.cayenne.DocumentVersion
import ish.oncourse.server.users.SystemUserService
import org.apache.cayenne.ObjectContext
import org.apache.commons.collections.CollectionUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static ish.oncourse.server.api.v1.function.DocumentFunctions.validateStoragePlace
import static ish.oncourse.server.api.function.EntityFunctions.checkForBadRequest
import static ish.oncourse.server.api.v1.function.DocumentFunctions.getDocumentByHash
import static ish.oncourse.server.api.v1.function.DocumentFunctions.createDocument
import static ish.oncourse.server.api.v1.function.DocumentFunctions.createDocumentVersion
import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocument
import static ish.oncourse.server.api.v1.function.DocumentFunctions.validateForSave
import static ish.oncourse.server.api.v1.function.DocumentFunctions.validateVersionForSave

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

        checkForBadRequest(validateStoragePlace(content.getBytes(), documentService, context))
        checkForBadRequest(validateForSave(fileName, name,  visibility, tagIds, shared, context))
        checkForBadRequest(validateVersionForSave(content.getBytes(), context))

        Document dbDocument = createDocument(name, description, visibility, tagIds, shared,  context)
        dbDocument.fileUUID = UUID.randomUUID().toString()
        DocumentVersionDTO initDocumentVersion = new DocumentVersionDTO().with {
            it.content = content.getBytes()
            it.fileName = fileName
            it
        }
        AmazonS3Service s3Service = documentService.usingExternalStorage ? new AmazonS3Service(documentService) : null
        DocumentVersion version = createDocumentVersion(dbDocument, initDocumentVersion, context, systemUserService.currentUser, s3Service)
        context.commitChanges()

        return toRestDocument(dbDocument, version.id, documentService)
    }

    @Override
    DocumentDTO get(Long id) {
        return toRestDocument(service.getEntityAndValidateExistence(cayenneService.newContext, id), null, documentService)
    }

    @Override
    DocumentDTO search(byte[] content) {
        ObjectContext context = cayenneService.newContext
        if (content && content.length) {
            Document document = getDocumentByHash(content, context)
            if (document) {
                return toRestDocument(document, null, documentService)
            }
        }
        return null
    }

    @Override
    void remove(Long id) {
        ObjectContext context = cayenneService.newContext
        service.makeDocumentInactive(id, context)
        context.commitChanges()
    }

    @Override
    void update(Long id, DocumentDTO documentDto) {
        ObjectContext context = cayenneService.newContext
        Document dbDocument = service.getEntityAndValidateExistence(context, id)

        service.validateModelBeforeSave(documentDto, context, id)
        service.toCayenneModel(documentDto, dbDocument)

        AmazonS3Service s3Service = documentService.usingExternalStorage ? new AmazonS3Service(documentService) : null

        List<DocumentVersionDTO> createdVersions = documentDto.versions.findAll { it.id == null }
        createdVersions.each { version ->
            checkForBadRequest(validateStoragePlace(version.content, documentService, context))
            checkForBadRequest(validateVersionForSave(version.content, context))
            createDocumentVersion(dbDocument, version, context, systemUserService.currentUser, s3Service)
        }

        List<Long> removedVersions = CollectionUtils.subtract(
                dbDocument.getVersions().collect { it.id },
                documentDto.getVersions().collect { it.id }
        ) as List<Long>
        dbDocument.versions
                .findAll { removedVersions.contains(it.id) }
                .each { version ->
                    if (s3Service != null && dbDocument.fileUUID != null) {
                        s3Service.removeFileVersion(dbDocument.fileUUID, version.versionId)
                    }
                    context.deleteObject(version)
                }

        List<Long> savedVersions = CollectionUtils.intersection(
                dbDocument.getVersions().collect { it.id },
                documentDto.getVersions().collect { it.id }
        ) as List<Long>
        dbDocument.versions
                .findAll { savedVersions.contains(it.id) }
                .each { version ->
                    try {
                        version.current = documentDto.versions.find { it.id == version.id }.current
                        if (s3Service != null && dbDocument.fileUUID != null) {
                            s3Service.changeVisibility(dbDocument.fileUUID, version.versionId, dbDocument.webVisibility)
                        }
                    } catch (Exception e) {
                        logger.error("Could not change document visibility uuid: {}, versionId: {}", dbDocument.getId(), version.versionId, e)
                    }
                }

        dbDocument.getContext().commitChanges()
    }

    @Override
    void bulkChange(DiffDTO diff) {
        service.bulkChange(diff)
    }

}
