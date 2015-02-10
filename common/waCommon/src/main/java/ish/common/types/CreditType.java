package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * Details of prior study for which credit/RPL was offered
 * http://heimshelp.deewr.gov.au/sites/heimshelp/2014_data_requirements/2014dataelements/pages/561
 * 
 * @PublicApi
 */
public enum CreditType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 0
	 * @PublicApi
	 */
	NO_CREDIT_RPL_WAS_OFFERED(0, "No credit/RPL was offered", "No credit/RPL was offered"),

	/**
	 * Database value: 100
	 * @PublicApi
	 */
	CREDIT_RPL_FOR_PRIOR_HIGHER_EDUCATION_STUDY_ONLY(100,
		"Credit/RPL was offered for prior higher education study only",
			"Credit/RPL was offered for prior higher education study only"),

	/**
	 * Database value: 200
	 * @PublicApi
	 */
	CREDIT_RPL_FOR_PRIOR_VET_STUDY_ONLY(200, "Credit/RPL was offered for prior VET study only",
		"Credit/RPL was offered for prior VET study only"),

	/**
	 * Database value: 300
	 * @PublicApi
	 */
	CREDIT_RPL_FOR_COMBINATION_OF_PRIOR_HIGHER_EDUCATION_AND_VET_STUDY(300,
		"Credit/RPL was offered for a combination of prior higher education and VET study",
			"Credit/RPL was offered for a combination of prior higher education and VET study"),

	/**
	 * Database value: 400
	 * @PublicApi
	 */
	CREDIT_RPL_FOR_STUDY_OUTSIDE_AUSTRALIA(400,
		"Credit/RPL was offered for study undertaken at an education provider outside Australia",
			"Credit/RPL was offered for study undertaken at an education provider outside Australia"),

	/**
	 * Database value: 500
	 * @PublicApi
	 */
	CREDIT_RPL_FOR_WORK_EXPERIENCE(500,
		"Credit/RPL was offered for work experience undertaken inside or outside Australia",
			"Credit/RPL was offered for work experience undertaken inside or outside Australia"),

	/**
	 * Database value: 600
	 * @PublicApi
	 */
	OTHER(600, "Other", "Other");

	private String displayValue;
	private String description;
	private int value;

	private CreditType(int value, String displayValue, String description) {
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
