package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * choices for attachment visibility
 * 
 * @PublicApi
 */
public enum AttachmentInfoVisibility implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * This attachment is only visible in the onCourse application.
	 * Links to the attachment will be creating using a special time-limited and securely signed URL.
	 * 
	 * Database value: 0
	 * 
	 * @PublicApi
	 */
	PRIVATE(0, "Private"),

	/**
	 * Everyone can see this attachment, including on the public facing website.
	 * 
	 * Database value: 1
	 * 
	 * @PublicApi
	 */
	PUBLIC(1, "Public"),

	/**
	 * These attachments cannot be seen by the general public, but are available to tutors and students
	 * enrolled in the course, typically within the skillsOnCourse portal. You would commonly only use this
	 * visibility type for attachments to courses and classes.
	 * 
	 * URLs to these attachments are time-limited and securely signed.
	 * 
	 * Database value: 2
	 * 
	 * @PublicApi
	 */
	STUDENTS(2, "Tutors and enrolled students"),

	/**
	 * These attachments cannot be seen by the general public, but are available to tutors
	 * teaching a course typically within the skillsOnCourse portal. You would commonly only use this
	 * visibility type for attachments to courses and classes, or to the tutor.
	 *
	 * URLs to these attachments are time-limited and securely signed.
	 *
	 * Database value: 3
	 * 
	 * @PublicApi
	 */
	TUTORS(3, "Tutors only");

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
