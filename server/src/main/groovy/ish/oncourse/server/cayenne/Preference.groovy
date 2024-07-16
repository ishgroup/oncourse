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
import ish.oncourse.server.cayenne.glue._Preference
import ish.persistence.Preferences
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import java.util.Date

import static ish.persistence.Preferences.SERVICES_COMMUNICATION_KEY

/**
 * Preferences are key-value entities containing settings defining onCourse behavior in various situations.
 */
@API
@QueueableEntity
class Preference extends _Preference implements Queueable {


	private static final Logger logger = LogManager.getLogger()

	/**
	 * Check if async replication is allowed on this object.
	 *
	 * @return isAsyncReplicationAllowed
	 */
	@Override
	boolean isAsyncReplicationAllowed() {
		return Preferences.DATE_MESSAGE_BEFORE_ARCHIVED == getName() ||
				getUser() == null && SERVICES_COMMUNICATION_KEY != getName() ||
				Preferences.DATE_MESSAGE_EXPECTED_BEFORE_ARCHIVED == getName()
	}

	@Override
	void prePersist() {
		if (getUser() != null) {
			setUniqueKey(getUser().getId() + getName())
		} else {
			setUniqueKey(getName())
		}
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
	 * @return name of the preference
	 */
	@Nonnull
	@API
	@Override
	String getName() {
		return super.getName()
	}

	/**
	 * @return unique key of the preference (equal to name if non user specific preference)
	 */
	@API
	@Override
	String getUniqueKey() {
		return super.getUniqueKey()
	}

	/**
	 * @return preference value in binary form
	 */
	@API
	@Override
	byte[] getValue() {
		return super.getValue()
	}

	/**
	 * @return preference string value
	 */
	@API
	@Override
	String getValueString() {
		return super.getValueString()
	}



	/**
	 * @return onCourse user to whom preference belongs, null if non user specific preference
	 */
	@API
	@Override
	SystemUser getUser() {
		return super.getUser()
	}

	/**
	 * @return prohibition to logging frequently updated preferences like soap communication key
	 */
	@Override
	boolean isAuditAllowed() {
		boolean isAllowed
		switch(this.getName()) {
			case SERVICES_COMMUNICATION_KEY : isAllowed = false
				break
			default:
				isAllowed = true
		}
		return isAllowed
	}
}
