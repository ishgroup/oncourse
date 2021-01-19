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

package ish.oncourse.server.document

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import io.bootique.annotation.BQConfigProperty
import ish.common.types.AttachmentInfoVisibility
import ish.oncourse.API
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.AttachableTrait
import ish.oncourse.server.cayenne.AttachmentData
import ish.oncourse.server.cayenne.Document
import ish.oncourse.server.cayenne.DocumentVersion
import ish.oncourse.server.scripting.api.DocumentSpec
import ish.util.RuntimeUtil

import static ish.oncourse.server.scripting.api.DocumentSpec.ATTACH_ACTION
import static ish.oncourse.server.scripting.api.DocumentSpec.CREATE_ACTION
import ish.s3.S3Service
import ish.s3.S3Service.UploadResult
import ish.util.ImageHelper
import ish.util.SecurityUtil
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.ObjectSelect

/**
 * This class contains simple image closure which allow to put any images from document management system to email template.
 * The syntax is
 * ```
 * ${image 'picture_1'}.
 *
 * ```
 * 'picture_1' is name of document in onCourse system.
 * If onCourse have more than one document with the same name then the first one will be chosen.
 * Make sure that required image has unique name to prevent such collision.
 * The image closure interpreted into simple img html tag like:
 * <img src="https://bucket-ish-oncourse.s3.amazonaws.com/c1ba6238-52f8-43e9-8298-e2bbfc493918?AWSAccessKeyId=NHMUJXKH3BBF6M6QEVOA&Expires=1458561937&Signature=Ej3ittJs7finYguCjnh5WsBhdeD%RR">
 */

@API
class DocumentService {

	private ICayenneService cayenneService

	DocumentService createDocumentService(ICayenneService cayenneService) {
		this.cayenneService = cayenneService
		this
	}

	private String bucketName
	private String accessKeyId
	private String accessSecretKey
	private String region
	private String storageLimit

	@BQConfigProperty
	void setBucket(String bucket) {
		RuntimeUtil.println("S3 bucket name is " + bucket)
		this.bucketName = bucket
	}

	@BQConfigProperty
	void setAccessKeyId(String accessKeyId) {
		RuntimeUtil.println("S3 access key Id is " + accessKeyId)
		this.accessKeyId = accessKeyId
	}

	@BQConfigProperty
	void setAccessSecretKey(String accessSecretKey) {
		RuntimeUtil.println("S3 access secret key is " + accessSecretKey)
		this.accessSecretKey = accessSecretKey
	}

	@BQConfigProperty
	void setRegion(String region) {
		this.region = region
	}

	@BQConfigProperty
	void setLimit(String limit) {
		RuntimeUtil.println("S3 bucket storage will limit to " + limit)
		this.storageLimit = limit
	}

	boolean isUsingExternalStorage() {
		this.accessKeyId != null
	}

	String getBucketName() {
		return bucketName
	}

	String getAccessKeyId() {
		return accessKeyId
	}

	String getAccessSecretKey() {
		return accessSecretKey
	}

	String getRegion() {
		return region
	}

	String getStorageLimit() {
		return storageLimit
	}


	@API
	def imageClosure = { String name ->
		Document doc = ObjectSelect.query(Document).where(Document.NAME.eq(name)).selectFirst(cayenneService.newContext)

		if (doc && doc.fileUUID && usingExternalStorage) {
			def url = s3Service.getFileUrl(doc.fileUUID, doc.webVisibility)
			return "<img src=\"$url\">"
		}
		return null
	}

    Document document(@DelegatesTo(DocumentSpec.class) Closure cl) {
		DocumentSpec documentSpec = new DocumentSpec()
        Closure build = cl.rehydrate(documentSpec, cl, this)
        build.setResolveStrategy(Closure.DELEGATE_FIRST)
        build.call()

        switch (documentSpec.action) {
			case ATTACH_ACTION:
				attachDocument(documentSpec.document, documentSpec.attach)
				return documentSpec.document
			case CREATE_ACTION:
				return createDocument(documentSpec.content, documentSpec.name, documentSpec.mimeType, documentSpec.permission, documentSpec.attach)
			default:
				throw new IllegalArgumentException('Unsupported document action type')

		}

	}

	@CompileStatic(TypeCheckingMode.SKIP)
	static void attachDocument(Document document, List<AttachableTrait> attach) {
		attach.each { attachable -> document.context.localObject(attachable).attachDocument(document) }
		document.context.commitChanges()
	}

	Document createDocument(byte[] content, String name, String mimeType, AttachmentInfoVisibility permission, List<AttachableTrait> attach) {
		DataContext context = cayenneService.newContext
		Document doc = context.newObject(Document)
		DocumentVersion version =  context.newObject(DocumentVersion)
		version.setDocument(doc)
		doc.setName(name)
		doc.setWebVisibility(permission)
		version.setMimeType(mimeType)
		version.setByteSize(content.size())
		if (ImageHelper.isImage(content, mimeType)) {
			version.setPixelWidth(ImageHelper.imageWidth(content))
			version.setPixelHeight(ImageHelper.imageHeight(content))
			version.setThumbnail(ImageHelper.generateThumbnail(content, mimeType))
        }
		version.setFileName(name)
		version.setHash(SecurityUtil.hashByteArray(content))
        Date now = new Date()
        version.setTimestamp(now)
        doc.setAdded(now)
        if (usingExternalStorage) {
			UploadResult result = s3Service.putFile(content, name, permission)

			doc.setFileUUID(result.getUuid())
            version.setVersionId(result.getVersionId())

        } else {
			AttachmentData attachmentData = context.newObject(AttachmentData)
            attachmentData.setContent(content)
            version.setAttachmentData(attachmentData)
        }

		attachDocument(doc, attach)
		return doc
	}

	private S3Service getS3Service() {
		return new S3Service(getAccessKeyId(), getAccessSecretKey(), getBucketName())
    }
}
