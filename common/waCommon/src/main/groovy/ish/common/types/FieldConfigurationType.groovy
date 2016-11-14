package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.API
/**
 * Each Field Configuration Scheme contains three FieldConfigurations, one for each type
 *
 */
@API
public enum FieldConfigurationType implements DisplayableExtendedEnumeration<Integer> {
	
	/**
	 * Database value: 1
	 * Apply ENROLMENT Field Configuration on enrol process
	 */
	@API
	ENROLMENT(1, "Enrolment"),

	/**
	 * Database value: 2
	 * Apply APPLICATION Field Configuration on application creation 
	 */
	@API
	APPLICATION(2, "Application"),

	/**
	 * Database value: 3
	 * Apply WAITING_LIST Field Configuration on Waiting List creation 
	 */
	@API
	WAITING_LIST(3, "Waiting List")

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDatabaseValue()
	 */


	private String displayName;
	private int value;

	private FieldConfigurationType(int value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}
	
	@Override
	public Integer getDatabaseValue() {
		return this.value;
	}

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return this.displayName;
	}
}
