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

package ish.oncourse.server.api.v1.function

import groovy.transform.CompileStatic
import ish.oncourse.cayenne.TaggableClasses
import ish.oncourse.server.api.dao.DocumentDao
import ish.oncourse.server.api.v1.model.*
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.document.DocumentService
import ish.s3.AmazonS3Service
import ish.util.LocalDateUtils
import ish.util.SecurityUtil
import ish.util.ThumbnailGenerator
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response
import java.nio.file.Files
import java.nio.file.Path

import static ish.oncourse.server.api.v1.function.ContactFunctions.getProfilePictureDocument
import static ish.oncourse.server.api.v1.function.TagFunctions.updateTags
import static ish.util.Constants.BILLING_APP_LINK
import static ish.util.ImageHelper.*
import static org.apache.commons.lang3.StringUtils.isBlank
import static org.apache.commons.lang3.StringUtils.trimToNull

@CompileStatic
class DocumentFunctions {

    private static final Logger logger = LogManager.getLogger(DocumentFunctions)

    static DocumentDTO getProfilePicture(Contact contact, DocumentService documentService) {
        Document profilePictureDocument = getProfilePictureDocument(contact)
        if (profilePictureDocument) {
            return toRestDocument(profilePictureDocument, profilePictureDocument.currentVersion.id, documentService)
        }
        null
    }

    static DocumentDTO toRestDocument(Document dbDocument, Long versionId, DocumentService documentService) {
        new DocumentDTO().with { document ->
            document.id = dbDocument.id
            document.name = dbDocument.name
            document.versionId = versionId
            document.added = LocalDateUtils.dateToTimeValue(dbDocument.added)
            document.tags = dbDocument.allTags.collect { it.id }
            DocumentVersion dbVersion = versionId ? dbDocument.versions.find { it.id == versionId } : dbDocument.currentVersion
            document.thumbnail = dbVersion?.thumbnail
            AmazonS3Service s3Service
            if (documentService.usingExternalStorage) {
                s3Service = new AmazonS3Service(documentService)
            }
            document.versions = dbDocument.versions.collect { toRestDocumentVersion(it, s3Service) }.sort { it.added }.reverse()

            document.description = dbDocument.description
            document.access = DocumentVisibilityDTO.values()[0].fromDbType(dbDocument.webVisibility)
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

    static DocumentDTO toRestDocumentMinimized(Document dbDocument, Long versionId, DocumentService documentService) {
        new DocumentDTO().with { document ->
            document.id = dbDocument.id
            document.name = dbDocument.name
            document.description = dbDocument.description
            document.added = LocalDateUtils.dateToTimeValue(dbDocument.added)
            document.access = DocumentVisibilityDTO.values()[0].fromDbType(dbDocument.webVisibility)
            document.shared = dbDocument.isShared
            document.removed = dbDocument.isRemoved
            AmazonS3Service s3Service = null
            if (documentService.usingExternalStorage) {
                s3Service = new AmazonS3Service(documentService)
            }
            document.versions = dbDocument.versions.collect { toRestDocumentVersionMinimized(it, s3Service) }.sort {it.added}.reverse()
            if (s3Service) {
                document.urlWithoutVersionId = s3Service.getFileUrl(dbDocument.fileUUID, null, dbDocument.webVisibility)
            }
            document
        }
    }

    static DocumentVersionDTO toRestDocumentVersion(DocumentVersion dbDocumentVersion, AmazonS3Service s3Service = null) {
        new DocumentVersionDTO().with { dv ->
            dv.id = dbDocumentVersion.id
            dv.added = LocalDateUtils.dateToTimeValue(dbDocumentVersion.timestamp)
            dv.createdBy = "${dbDocumentVersion.createdByUser?.firstName} ${dbDocumentVersion.createdByUser?.lastName}"
            dv.size = getDisplayableSize(dbDocumentVersion.byteSize)
            dv.mimeType = dbDocumentVersion.mimeType
            dv.fileName = dbDocumentVersion.fileName
            dv.thumbnail = dbDocumentVersion.thumbnail
            dv.current = dbDocumentVersion.current
            if (s3Service) {
                dv.url = s3Service.getFileUrl(dbDocumentVersion.document.fileUUID, dbDocumentVersion.versionId, dbDocumentVersion.document.webVisibility)
            }
            dv
        }
    }

    static DocumentVersionDTO toRestDocumentVersionMinimized(DocumentVersion dbDocumentVersion, AmazonS3Service s3Service = null) {
        new DocumentVersionDTO().with { dv ->
            dv.id = dbDocumentVersion.id
            dv.added = LocalDateUtils.dateToTimeValue(dbDocumentVersion.timestamp)
            dv.size = getDisplayableSize(dbDocumentVersion.byteSize)
            dv.thumbnail = dbDocumentVersion.thumbnail
            if (s3Service) {
                dv.url = s3Service.getFileUrl(dbDocumentVersion.document.fileUUID, dbDocumentVersion.versionId, dbDocumentVersion.document.webVisibility)
            }
            dv
        }
    }

    static  List<DocumentAttachmentRelationDTO> toRestDocumentAttachmentRelations(List<AttachmentRelation> attachmentRelations) {
        List<DocumentAttachmentRelationDTO> list = new ArrayList<>()
        attachmentRelations.sort { it.modifiedOn }.reverse().each { attachmentRelation ->
            list.add(
                    new DocumentAttachmentRelationDTO().with { dar ->
                        dar.entity = attachmentRelation.entityIdentifier.with { it.equalsIgnoreCase('CourseClass') ? 'Class' : it }
                        dar.label = createLabel(attachmentRelation.attachedRelation)
                        dar.entityId = attachmentRelation.attachedRelation.id
                        Contact relatedContact = attachmentRelation.contact
                        if (relatedContact) {
                            dar.relatedContacts << new SearchItemDTO(id: relatedContact.id, name: relatedContact.fullName)
                        }
                        dar
                    }
            )
        }
        list
    }

    static Document createDocument(String name, String description, DocumentVisibilityDTO access, List<Long> tags, Boolean shared, ObjectContext context) {
        Date timestamp = new Date()
        Document dbDocument = context.newObject(Document)
        dbDocument.added = timestamp
        dbDocument.name = trimToNull(name)
        dbDocument.webVisibility = access.dbType
        dbDocument.description = trimToNull(description)
        dbDocument.isShared = shared

        updateTags(dbDocument, dbDocument.taggingRelations, tags, DocumentTagRelation, context)

        dbDocument
    }

    static DocumentVersion createDocumentVersion(Document document, DocumentVersionDTO versionDTO, ObjectContext context, SystemUser user, AmazonS3Service s3Service = null) {
        Date timestamp = new Date()
        String filename = versionDTO.fileName
        String hash  = SecurityUtil.hashByteArray(versionDTO.content)

        DocumentVersion version = context.newObject(DocumentVersion)
        version.document = document
        version.hash = hash
        version.byteSize = versionDTO.content.length as Long
        version.mimeType = Files.probeContentType(Path.of(filename))
        version.timestamp = timestamp
        version.fileName = trimToNull(filename)
        version.createdByUser = context.localObject(user)
        version.current = versionDTO.current
        if (isImage(versionDTO.content, version.mimeType)) {
            version.pixelWidth = imageWidth(versionDTO.content)
            version.pixelHeight = imageHeight(versionDTO.content)
            try {
                version.thumbnail = ThumbnailGenerator.generateForImg(versionDTO.content, version.mimeType)
            } catch (IOException e) {
                logger.warn("Attempted to process document with name $document.name as an image, but it wasn't.")
                logger.catching(e)
            }
        } else {
            try {
                switch (version.mimeType) {
                    case {it instanceof String && isDoc(it as String)}:
                        version.thumbnail = ThumbnailGenerator.generateForDoc(versionDTO.content)
                        break
                    case {it instanceof String && isExcel(it as String)}:
                        version.thumbnail = ThumbnailGenerator.generateForExcel(versionDTO.content)
                        break
                    case {it instanceof String && isCsv(it as String)}:
                        version.thumbnail = ThumbnailGenerator.generateForCsv(versionDTO.content)
                        break
                    case {it instanceof String && isText(it as String)}:
                        version.thumbnail = ThumbnailGenerator.generateForText(versionDTO.content)
                        break
                    default:
                        version.thumbnail = generatePdfPreview(versionDTO.content)
                }
            } catch (NotActiveException e) {
                logger.warn("Attempted to process document with name $document.name failed. Angel can not generate privew")
                logger.catching(e)
            }
        }

        if (s3Service) {
            version.versionId = s3Service.putFile(document.fileUUID, version.fileName, versionDTO.content, document.webVisibility)
        } else {
            // throw new ClientErrorException("Attempted to process document with name $document.name as data, stored in db, but this ability was removed in new versions. Add s3 accessKey.", Response.Status.BAD_REQUEST)
        }

        version
    }


    static ValidationErrorDTO validateStoragePlace(byte[] content, DocumentService documentService, ObjectContext context) {
        Long currentStorageSize = DocumentDao.getStoredDocumentsSize(context)?:0
        if (documentService.storageLimit != null && currentStorageSize + content.length > documentService.storageLimit) {
            String otherwise = BILLING_APP_LINK ? "add additional storage <a href=\"${BILLING_APP_LINK}\">here.</a>" : "contact ish support, please."
            String message
            if (documentService.storageLimit == 0) {
                message = "Your license doesn't allow to storage documents. Contact ish support, please!"
            } else {
                message = "You require additional document storage capacity to save this document. " +
                        "Either make space by deleting some of your existing documents or ${otherwise}"
            }
            return new ValidationErrorDTO(null, 'content', message)
        }
    }

    static ValidationErrorDTO validateVersionForSave(byte[] content, ObjectContext context) {
        if (!content && content.length) {
            return new ValidationErrorDTO(null, 'versions', 'Your upload has failed. A least one version of document required.')
        }

        Document document = getDocumentByHash(content, context)
        if (document) {
            return new ValidationErrorDTO(null, 'versions',
                    "Your upload has failed. The file you are trying to upload already exists as a document called '${document.name}' or its history.")
        }

        return null
    }


    static ValidationErrorDTO validateForSave(String filename, String name, DocumentVisibilityDTO access, List<Long> tags, Boolean shared, ObjectContext context) {
        if (isBlank(name)) {
            return new ValidationErrorDTO(null, 'name', 'Name is required.')
        }
        if (isBlank(filename)) {
            return new ValidationErrorDTO(null, 'filename', 'File name is required.')
        }
        if (!access) {
            return new ValidationErrorDTO(null, 'access', 'Access is required.')
        }

        if (shared == null) {
            return new ValidationErrorDTO(null, 'shared', 'Attaching to multiple records is required.')
        }

        return TagFunctions.validateRelationsForSave(Document, context, tags, TaggableClasses.DOCUMENT)
    }

    static Document getDocumentByHash(byte[] content, ObjectContext context) {
        try {
            String hash = SecurityUtil.hashByteArray(content)

            return DocumentDao.getByHash(context, hash)
        } catch (IOException e) {
            logger.error("Error reading uploaded file", e)
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(null, 'content', 'Cannot calculate file hash.')).build())
        }
    }

    static String createLabel(AttachableTrait entity) {
        switch (entity.class) {
            case Application:
                (entity as Application).course.name + ', ' + (entity as Application).student.fullName
                break
            case Assessment:
                (entity as Assessment).name
                break
            case Contact:
                (entity as Contact).fullName
                break
            case Course:
                (entity as Course).name
                break
            case CourseClass:
                (entity as CourseClass).uniqueCode
                break
            case Enrolment:
                (entity as Enrolment).courseClass.uniqueCode + ', ' + (entity as Enrolment).student.fullName
                break
            case PriorLearning:
                (entity as PriorLearning).title + ', ' + (entity as PriorLearning).student.fullName
                break
            case Invoice:
                (entity as Invoice).entityName.capitalize() + ' ' + (entity as Invoice).invoiceNumber
                break
            case Room:
                (entity as Room).site.name + ', ' + (entity as Room).name
                break
            case Site:
                (entity as Site).name
                break
            case Article:
            case Voucher:
            case Membership:
                (entity as ProductItem).product.name
                break
            case ArticleProduct:
            case VoucherProduct:
            case MembershipProduct:
                (entity as Product).name
                break
            default:
                " "
        }
    }


    /**
     * @relatedObject -  entity taht can have attached documents: Contact/Course/Enrolment/...
     * @documentRelations - list of many-to-many relation object: ContactAttachmentRelation/CourseAttachmentRelation/...
     * @relationClass - type of many-to-many relation: ContactAttachmentRelation/CourseAttachmentRelation/...
     *
     *
     * Main purpose of this method habdle list of actual attached Documents list:
     *  - remove relations if user unattached Document
     *  - create new relation if user add Document
     **/
    static void updateDocuments(AttachableTrait relatedObject,
                                List<? extends AttachmentRelation> documentRelations,
                                List<DocumentDTO> documents,
                                Class<? extends AttachmentRelation> relationClass,
                                ObjectContext context) {

        if (!documents) {
            documents = []
        }
        List<Long> documentsToSave = documents*.id
        List<Long> currentDocs = documentRelations*.document*.id

        documentRelations.findAll { !(it.document.id in documentsToSave) }.each {
            context.deleteObjects(it)
        }

        documentRelations.findAll { it.document.id in documentsToSave }.each { relation ->
            DocumentDTO document = documents.find { it.id == relation.document.id }
            if (document.versionId) {
                relation.documentVersion = SelectById.query(DocumentVersion, document.versionId).selectOne(context)
            }
        }


        ObjectSelect.query(Document)
                .where(Document.ID.in(documentsToSave.findAll { !(it in currentDocs) }))
                .prefetch(Document.VERSIONS.joint())
                .select(context)
                .each { dbDocument ->
            context.newObject(relationClass).with { relation ->
                relation.attachedRelation = relatedObject
                relation.document = dbDocument
                relation.entityIdentifier = relatedObject.class.simpleName
                Long versionId = documents.find { it.id == dbDocument.id }?.versionId
                if (versionId) {
                    relation.documentVersion = dbDocument.versions.find { it.id == versionId }
                }
                relation
            }
        }
    }

    static String getDisplayableSize(Long size) {
        size < 1024 ? "$size b" :
                size < 1024 * 1024 ? "${(size / 1024).round()} kb" :
                        size < 1024 * 1024 * 1024 ? "${(size / 1024 / 1024).round()} Mb" :
                                 "${(size / 1024 / 1024 / 1024).round()} Gb"
    }
}
