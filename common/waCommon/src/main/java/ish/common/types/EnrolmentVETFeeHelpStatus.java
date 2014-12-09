package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * VET FEE HELP status.
 * values: not eligible, HELP not requested, HELP requested;
 * 
 * @PublicApi
 */
public enum EnrolmentVETFeeHelpStatus implements DisplayableExtendedEnumeration<Integer> {
	
	/**
	 * @PublicApi
	 */
	NOT_ELIGIBLE(0, "Not eligible", "Not eligible"),

	/**
	 * @PublicApi
	 */
	HELP_NOT_REQUESTED(1, "HELP not requested", "HELP not requested"),

	/**
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
