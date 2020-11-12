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
import ish.oncourse.server.cayenne.glue._AttachmentData

import javax.annotation.Nonnull
import java.util.Date

/**
 * Represents attachment file stored in onCourse database.
 * Note that this class is not used for customers using Amazon S3 for their storage.
 */
@API
class AttachmentData extends _AttachmentData {


	/**
	 * @return binary content of the attachment
	 */
	@Nonnull
	@API
	@Override
	byte[] getContent() {
		return super.getContent()
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
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return document version meta data associated with this attachment
	 */
	@Nonnull
	@API
	@Override
	DocumentVersion getDocumentVersion() {
		return super.getDocumentVersion()
	}
}
