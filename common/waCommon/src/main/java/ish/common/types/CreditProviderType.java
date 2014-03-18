package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * Type of provider where VET study was undertaken
 * http://heimshelp.deewr.gov.au/sites/heimshelp/2014_data_requirements/2014dataelements/pages/564
 * @author vdavidovich
 */
public enum CreditProviderType implements DisplayableExtendedEnumeration<Integer> {
	NO_CREDIT_RPL_WAS_OFFERED_FOR_VET(0, "No credit/RPL was offered for VET",
		"No credit/RPL was offered for VET"),
	UNIVERSITY(10, "University", "University"),
	OTHER_HIGHER_EDUCATION_PROVIDER(19, "Other Higher Education Provider",
		"Other Higher Education Provider"),
	TAFE(20, "TAFE", "TAFE"),
	SECONDARY_SCHOOLS_OR_COLLEGES(21, "Secondary Schools/Colleges",
		"Secondary Schools/Colleges"),
	OTHER_REGISTERED_TRAINING_ORGANIZATIONS(29, "Other Registered Training Organisations",
		"Other Registered Training Organisations"),
	NOT_ELSEWHERE_CATEGORIZED(90, "Not elsewhere categorised",
		"Not elsewhere categorised");

	private String displayValue;
	private String description;
	private int value;

	private CreditProviderType(int value, String displayValue, String description) {
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
