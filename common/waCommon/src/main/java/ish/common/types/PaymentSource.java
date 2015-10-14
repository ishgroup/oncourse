package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * Payment can be made in onCourse (office) or from the onCourse website (web).
 * Although named "PaymentSource" this enumeration is used for enrolments and other entities to mark where they were originated.
 * 
 */
@API
public enum PaymentSource implements DisplayableExtendedEnumeration<String> {

	/**
	 * all payments made from onCourse will have this source
	 * 
	 * Database value: O
	 */
	@API
	SOURCE_ONCOURSE("O", "office"),
	
	/**
	 * all payments made from onCourse website will have this source
	 * 
	 * Database value: W
	 */
	@API
	SOURCE_WEB("W", "web");

	private PaymentSource(String value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	private String value;
	private String displayName;

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDatabaseValue()
	 */
	@Override
	public String getDatabaseValue() {
		return this.value;
	}

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return getDisplayName();
	}
}
