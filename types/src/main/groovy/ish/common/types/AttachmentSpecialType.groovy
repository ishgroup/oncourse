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
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * A special type of attachment. This will result in special behaviour inside onCourse.
 *
 */
@API
public enum AttachmentSpecialType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * The profile picture for a contact is seen in Quick Enrol, the contact edit view
	 * and within the skillsOnCourse portal. There can be only one of these attachments per Contact.
	 *
	 * Database value: 0
	 *
	 */
	@API
	PROFILE_PICTURE(0, "Profile picture");

	private int value;
	private String displayName;

	private AttachmentSpecialType(int value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDatabaseValue()
	 */
	@Override
	public Integer getDatabaseValue() {
		return value;
	}

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return getDisplayName();
	}
}
