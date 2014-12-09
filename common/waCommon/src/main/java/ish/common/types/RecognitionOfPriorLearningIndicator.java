package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * A code indicating whether an RPL is a unit of study or has an RPL component in the unit of study.
 * http://heimshelp.deewr.gov.au/sites/heimshelp/2014_data_requirements/2014dataelements/pages/577
 * 
 * @PublicApi
 */
public enum RecognitionOfPriorLearningIndicator implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	NOT_RPL_UNIT_OF_STUDY(0, "Unit of study is NOT an RPL unit of study",
		"Unit of study is NOT an RPL unit of study"),

	/**
	 * @PublicApi
	 */
	UNIT_OF_STUDY_CONSISTS_WHOLLY_OF_RPL(1, "Unit of study consists wholly of RPL",
		"Unit of study consists wholly of RPL"),

	/**
	 * @PublicApi
	 */
	UNIT_OF_STUDY_HAS_A_COMPONENT_OF_RPL(2, "Unit of study has a component of RPL",
		"Unit of study has a component of RPL");

	private String displayValue;
	private String description;
	private int value;

	private RecognitionOfPriorLearningIndicator(int value, String displayValue, String description) {
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
