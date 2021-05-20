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

import com.google.inject.Inject
import ish.common.types.AttachmentInfoVisibility
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._Document
import ish.oncourse.server.document.DocumentService
import ish.oncourse.server.license.LicenseService
import org.apache.cayenne.query.Ordering

import javax.annotation.Nonnull

/**
 * Document is a file (like text document, image, pdf, etc.) which can be attached to different types of
 * records in onCourse. Documents can have multiple versions allowing to track historic information for
 * RTO compliance purposes.
 */
@API
@QueueableEntity
class Document extends _Document implements DocumentTrait, Queueable {


	public static final String LINK_PROPERTY = "link"
	public static final String ACTIVE_PROPERTY = "active"
	public static final String CURRENT_VERSION_PROPERTY = "currentVersion"

	@Inject
	private DocumentService documentService

	@Inject
	private LicenseService licenseService

	@Override
	protected void postAdd() {
		super.postAdd()

		if (getIsRemoved() == null) {
			setIsRemoved(false)
		}

		if (getIsShared() == null) {
			setIsShared(true)
		}
	}

	/**
	 * @return latest version of this document
	 */
	@API
	DocumentVersion getCurrentVersion() {
		List<DocumentVersion> versions = getVersions()

		Ordering.orderList(versions, Collections.singletonList(DocumentVersion.TIMESTAMP.desc()))

		return versions.get(0)
	}

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return text description of this document
	 */
	@API
	@Override
	String getDescription() {
		return super.getDescription()
	}

	/**
	 * Unique file identifier is assigned to every document stored in Amazon S3.
	 * This id is used to generate HTTP links to the document.
	 *
	 * @return unique id for this attachment in Amazon S3 storage
	 */
	@API
	@Override
	String getFileUUID() {
		return super.getFileUUID()
	}


	/**
	 * @return true if the document has been marked as removed
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsRemoved() {
		return super.getIsRemoved()
	}

	/**
	 * @return true if document can be attached to multiple records at once
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsShared() {
		return super.getIsShared()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return name of this document
	 */
	@Nonnull
	@API
	@Override
	String getName() {
		return super.getName()
	}

	/**
	 * @return visibility setting for this document: private, public, enrolled students and tutors or tutors only
	 */
	@Nonnull
	@API
	@Override
	AttachmentInfoVisibility getWebVisibility() {
		return super.getWebVisibility()
	}

	/**
	 * @return historical list of versions of this document
	 */
	@Nonnull
	@API
	@Override
	List<DocumentVersion> getVersions() {
		return super.getVersions()
	}

	/**
	 * @return The list of tags assigned to document
	 */
	@Nonnull
	@API
	List<Tag> getTags() {
		List<Tag> tagList = new ArrayList<>(getTaggingRelations().size())
		for (DocumentTagRelation relation : getTaggingRelations()) {
			tagList.add(relation.getTag())
		}
		return tagList
	}

	/**
	 * @return A list of records attached to this document
	 */
	@Nonnull
	List<AttachableTrait> getRelatedRecords() {
		return attachmentRelations*.attachedRelation
	}

	@API
	Boolean isActive() {
		return !super.isRemoved
	}

	@API
	/**
	 * Generates URL to access file.
	 *
	 * for 'Public' documents it is static address taht can be acceassable in any time.
	 * for non 'Public' documents generate signed link which can be used in next 10 minutes only
	 */
	String getLink() {
		def s3Service = getS3ServiceInstance(documentService)
		return s3Service != null ? s3Service.getFileUrl(getFileUUID(), getWebVisibility()) : ""
	}

	@API
	/**
	 * Generates local URL to document on onCourse if collegeKey is specified in onCourse.yml.
	 */
	String getLinkOnCourse() {
		String collegeKey = licenseService.getCollege_key()
		return collegeKey != null ? "https://${collegeKey}.cloud.oncourse.cc/document/${id}" : ""
	}
}



