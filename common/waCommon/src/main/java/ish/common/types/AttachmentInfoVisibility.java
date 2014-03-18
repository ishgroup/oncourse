package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * choices for attachment visibility
 * 
 * @author marcin
 */
public enum AttachmentInfoVisibility implements DisplayableExtendedEnumeration<Integer> {
	PRIVATE(0, "Private"),
	PUBLIC(1, "Public"),
	STUDENTS(2, "Tutors and enrolled students"),
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
