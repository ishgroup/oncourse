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

import ish.common.types.MessageTemplateType
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._MessageTemplate
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull

/**
 */
@QueueableEntity
class MessageTemplate extends _MessageTemplate implements Queueable {

	public static final String NAME_KEY = "name";
	public static final String MESSAGE_KEY = "message";
	public static final String SUBJECT_KEY = "subject";
	private static final Logger logger = LogManager.getLogger()

	/**
	 * @return the date and time this record was created
	 */
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}


	/**
	 * @return
	 */
	@Override
	String getMessage() {
		return super.getMessage()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return
	 */
	@Nonnull
	@Override
	String getName() {
		return super.getName()
	}

	/**
	 * @return
	 */
	@Override
	String getSubject() {
		return super.getSubject()
	}

	/**
	 * @return
	 */
	@Nonnull
	@Override
	MessageTemplateType getType() {
		return super.getType()
	}


}
