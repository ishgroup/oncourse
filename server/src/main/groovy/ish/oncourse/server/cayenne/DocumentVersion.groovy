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
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.api.v1.function.DocumentFunctions
import ish.oncourse.server.cayenne.glue._DocumentVersion

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.util.Date
import java.util.List

/**
 * Specific version of a document record stored in onCourse.
 */
@API
@QueueableEntity
class DocumentVersion extends _DocumentVersion implements Queueable {



	/**
	 * @return size of attachment in bytes
	 */
	@API
	@Override
	Long getByteSize() {
		return super.getByteSize()
	}

	/**
	 * @return displayble size of attachment in bytes
	 */
	@API
	String getDisplayableSize() {
		return DocumentFunctions.getDisplayableSize(getByteSize())
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
	 * @return description of this document version
	 */
	@API
	@Override
	String getDescription() {
		return super.getDescription()
	}

	/**
	 * @return default file name for this document version
	 */
	@API
	@Override
	String getFileName() {
		return super.getFileName()
	}

	/**
	 * @return SHA-1 hash of the attachment file data
	 */
	@API
	@Override
	String getHash() {
		return super.getHash()
	}

	/**
	 * @return MIME type of the attachment
	 */
	@API
	@Override
	String getMimeType() {
		return super.getMimeType()
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
	 * @return height of the image attachment in pixels, null if attachment is not image
	 */
	@API
	@Override
	Integer getPixelHeight() {
		return super.getPixelHeight()
	}

	/**
	 * @return width of the image attachment in pixels, null if attachment is not image
	 */
	@API
	@Override
	Integer getPixelWidth() {
		return super.getPixelWidth()
	}

	/**
	 * @return 140x140 thumbnail of the image attachment, null if attachment is not image
	 */
	@API
	@Override
	byte[] getThumbnail() {
		return super.getThumbnail()
	}

	/**
	 * @return date and time when this document version was created
	 */
	@Nonnull
	@API
	@Override
	Date getTimestamp() {
		return super.getTimestamp()
	}

	/**
	 * @return identifier of this document version used by Amazon S3 storage
	 */
	@API
	@Override
	String getVersionId() {
		return super.getVersionId()
	}



	/**
	 * @return attachment file data stored in onCourse database (only works if Amazon S3 is not configured)
	 */
	@Nonnull
	@API
	@Override
	AttachmentData getAttachmentData() {
		return super.getAttachmentData()
	}

	/**
	 * @return list of relations for this document version
	 */
	@Nonnull
	@Override
	List<AttachmentRelation> getAttachmentRelations() {
		return super.getAttachmentRelations()
	}

	/**
	 * @return onCourse user who created this document version
	 */
	@Nullable
	@API
	@Override
	SystemUser getCreatedByUser() {
		return super.getCreatedByUser()
	}

	/**
	 * @return linked document
	 */
	@Nonnull
	@API
	@Override
	Document getDocument() {
		return super.getDocument()
	}

	@Override
	String getSummaryDescription() {
		return getFileName()
	}
}



