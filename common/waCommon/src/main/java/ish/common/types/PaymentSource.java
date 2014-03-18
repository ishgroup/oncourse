package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * enumeration containing two choices of payment sources: website or office
 */
public enum PaymentSource implements DisplayableExtendedEnumeration<String> {

	/**
	 * all payments made from onCourse angel will have this source
	 */
	SOURCE_ONCOURSE("O", "office"),
	/**
	 * all payments made from onCourse website will have this source
	 */
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
