/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * A special type of attachment. Right now there is only one special
 * type.
 * 
 * @PublicApi
 */
public enum AttachmentSpecialType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * The profile picture for a contact is seen in Quick Enrol, the contact edit view
	 * and within the skillsOnCourse portal.
	 * 
	 * @PublicApi
	 */
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
