package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.API
/**
 * Type of Field Configuration is condition under which it will be used.
 */
@API
enum FieldConfigurationType implements DisplayableExtendedEnumeration<Integer> {
	
	/**
	 * Database value: 1
	 * Field Configuration will be used during web enrol process
	 */
	@API
	ENROLMENT(1, "Enrolment"),

	/**
	 * Database value: 2
	 * Field Configuration will be used during web application process
	 */
	@API
	APPLICATION(2, "Application"),

	/**
	 * Database value: 3
	 * Field Configuration will be used when joining waiting lists on web
	 */
	@API
	WAITING_LIST(3, "Waiting List"),

	/**
	 * Database value: 4
	 * Field Configuration will be used for surveys
	 */
	@API
	SURVEY(4, "Survey"),

	/**
	 * Database value: 5
	 * Field Configuration will be used for payers
	 */
	@API
	PAYER(5, "Payer"),

	/**
	 * Database value: 6
	 * Field Configuration will be used for parents or guardians
	 */
	@API
	PARENT(6, "Parent")
	
	private String displayName
	private int value

	private FieldConfigurationType(int value, String displayName) {
		this.value = value
		this.displayName = displayName
	}
	
	@Override
	Integer getDatabaseValue() {
		return this.value
	}

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDisplayName()
	 */
	@Override
	String getDisplayName() {
		return this.displayName
	}
}
