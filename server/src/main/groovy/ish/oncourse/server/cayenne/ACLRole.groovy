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

import ish.common.types.KeyCode
import ish.oncourse.API
import ish.oncourse.server.cayenne.glue._ACLRole

import javax.annotation.Nonnull
import javax.annotation.Nullable

/**
 * An ACLRole (or just "Role") is a grouping of authorisation rights @see ish.oncourse.server.cayenne.ACLAccessKey
 * to various parts of the system. Every login user @see ish.oncourse.server.cayenne.SystemUser is assigned to a single
 * ACLRole, or no role at all if they are an adminstrator.
 *
 */
@API
class ACLRole extends _ACLRole {



	/**
	 * @param keyCode the KeyCode to gain ACLAccessKey for
	 * @return ACLAccessKey of the given keyCode
	 */
	@Nullable
	@API
	ACLAccessKey getAccessKeyForKeyCode(KeyCode keyCode) {
		for (ACLAccessKey accesKey : getAccessKeys()) {
			if (accesKey.getKeycode() == keyCode) {
				return accesKey
			}
		}
		return null
	}

	void createACLAccessKey(KeyCode keycode, Integer mask) {
		ACLAccessKey key = getAccessKeyForKeyCode(keycode)
		if (key == null) {
			key = getObjectContext().newObject(ACLAccessKey.class)
			key.setRole(this)
			key.setKeycode(keycode)
		}
		key.setMask(mask)
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
	 * @return the name of the Role
	 */
	@Nonnull
	@API
	@Override
	String getName() {
		return super.getName()
	}



	/**
	 * @return a list of all access keys for this role
	 */
	@Nonnull
	@API
	@Override
	List<ACLAccessKey> getAccessKeys() {
		return super.getAccessKeys()
	}

	/**
	 * @return a list of all users attached to this role
	 */
	@Nonnull
	@API
	@Override
	List<SystemUser> getSystemUsers() {
		return super.getSystemUsers()
	}
}
