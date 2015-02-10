package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * VET FEE HELP is an Australian government loan programme for funding education. Each enrolment in an eligible FEE HELP class can have one of these states.
 * These states only make sense if the CourseClass has FEE HELP enabled.
 * 
 * @PublicApi
 */
public enum EnrolmentVETFeeHelpStatus implements DisplayableExtendedEnumeration<Integer> {
	
	/**
	 * Database value: 0
	 * @PublicApi
	 */
	NOT_ELIGIBLE(0, "Not eligible", "Not eligible"),

	/**
	 * Database value: 1
	 * @PublicApi
	 */
	HELP_NOT_REQUESTED(1, "HELP not requested", "HELP not requested"),

	/**
	 * Database value: 2
	 * @PublicApi
	 */
	HELP_REQUESTED(2, "HELP requested", "HELP requested");

	private String displayValue;
	private String description;
	private int value;

	private EnrolmentVETFeeHelpStatus(int value, String displayValue, String description) {
		this.displayValue = displayValue;
		this.description = description;
		this.value = value;
	}

	@Override
	public String getDisplayName() {
		return displayValue;
	}

	@Override
	public Integer getDatabaseValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
