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
 * Document visibility. Although the class is named AttachmentInfoVisibility it applies to the document itself.
 */
@API
enum AttachmentInfoVisibility implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * This document is only visible in the onCourse application.
	 * The document is viewable only using a time-limited and securely signed URL. This makes it harder for users
	 * to share the URL with other people.
	 */
	@API
	PRIVATE(0, "Private"),

	/**
	 * These documents cannot be seen by the general public, but are available to tutors
	 * within the skillsOnCourse portal. You would commonly only use this
	 * visibility type for documents attached to courses and classes, or to tutors.
	 *
	 * URLs to these documents are time-limited and securely signed. This makes it harder for users to share the
	 * URL with other people.
	 */
	@API
	TUTORS(3, "Tutors only"),

	/**
	 * These attachments cannot be seen by the general public, but are available to tutors and students
	 * enrolled in the course, typically within the skillsOnCourse portal. You would commonly only use this
	 * visibility type for attachments to courses and classes.
	 *
	 * URLs to these documents are time-limited and securely signed. This makes it harder for users to share the
	 * URL with other people.
	 */
	@API
	STUDENTS(2, "Tutors and enrolled students"),

	/**
	 * This document can be shared with a URL that is not time limited. A user with this link could share it with anyone.
	 */
	@API
	LINK(4, "Linkable"),

	/**
	 * This document can be shared with a URL that is not time limited. A user with this link could share it with anyone.
	 * This document may also appear on the public facing website without any restrictions, depending on what it
	 * is attached to. For example, if attached to a Course it might appear on the course page.
	 */
	@API
	PUBLIC(1, "Website");


	private final int value;
	private String displayName;

	private AttachmentInfoVisibility(int value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDatabaseValue()
	 */
	@Override
	Integer getDatabaseValue() {
		return value;
	}

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDisplayName()
	 */
	@Override
	String getDisplayName() {
		return displayName;
	}

	/**
	 * @see java.lang.Enum#toString()
	 */
	@Override
	String toString() {
		return getDisplayName();
	}

}
