package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * Level of education of prior VET study for which credit/RPL was offered
 * http://heimshelp.deewr.gov.au/sites/heimshelp/2014_data_requirements/2014dataelements/pages/563
 * 
 * @PublicApi
 */
public enum CreditLevel implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	NO_CREDIT_RPL_WAS_OFFERED_FOR_VET(0, "No credit/RPL was offered for VET", "No credit/RPL was offered for VET"),
	
	/*Vocational graduate levels*/
	/**
	 * @PublicApi
	 */
	VOCATIONAL_GRADUATE_CERTIFICATE(1, "Vocational Graduate Certificate", "Vocational Graduate Certificate"),

	/**
	 * @PublicApi
	 */
	VOCATIONAL_GRADUATE_DIPLOMA(2, "Vocational Graduate Diploma", "Vocational Graduate Diploma"),
	
	/*Advanced diploma levels*/
	/**
	 * @PublicApi
	 */
	ADVANCED_DIPLOMA(411, "Advanced Diploma", "Advanced Diploma"),

	/**
	 * @PublicApi
	 */
	STATEMENT_OF_ATTEINMENT_AT_ADVANCED_DIPLOMA_LEVEL(412, "Statement of Attainment at Advanced Diploma level",
		"Statement of Attainment at Advanced Diploma level"),

	/**
	 * @PublicApi
	 */
	BRIDGING_AND_ENABLING_COURSE_AT_ADVANCED_DIPLOMA(415, "Bridging and Enabling Course at Advanced Diploma",
		"Bridging and Enabling Course at Advanced Diploma"),
	
	/*Diploma levels*/
	/**
	 * @PublicApi
	 */
	DIPLOMA(421, "Diploma", "Diploma"),

	/**
	 * @PublicApi
	 */
	STATEMENT_OF_ATTEINMENT_AT_DIPLOMA_LEVEL(422, "Statement of Attainment at Diploma level",
		"Statement of Attainment at Diploma level"),

	/**
	 * @PublicApi
	 */
	BRIDGING_AND_ENABLING_COURSE_AT_DIPLOMA(423, "Bridging and Enabling Course at Diploma",
		"Bridging and Enabling Course at Diploma"),
	
	/*Certificate III and IV level*/
	/**
	 * @PublicApi
	 */
	CERTIFICATE_4_LEVEL(511, "Certificate IV", "Certificate IV"),
	
	/**
	 * @PublicApi
	 */
	STATEMENT_OF_ATTEINMENT_AT_CERTIFICATE_4_LEVEL(512, "Statement of Attainment at Certificate IV level",
		"Statement of Attainment at Certificate IV level"),

	/**
	 * @PublicApi
	 */
	BRIDGING_AND_ENABLING_COURSE_AT_CERTIFICATE_4_LEVEL(513, "Bridging and Enabling Course at Certificate IV level",
		"Bridging and Enabling Course at Certificate IV level"),

	/**
	 * @PublicApi
	 */
	CERTIFICATE_3_LEVEL(514, "Certificate III", "Certificate III"),

	/**
	 * @PublicApi
	 */
	STATEMENT_OF_ATTEINMENT_AT_CERTIFICATE_3_LEVEL(515, "Statement of Attainment at Certificate III level",
		"Statement of Attainment at Certificate III level"),

	/**
	 * @PublicApi
	 */
	BRIDGING_AND_ENABLING_COURSE_AT_CERTIFICATE_3_LEVEL(516, "Bridging and Enabling Course at Certificate III level",
		"Bridging and Enabling Course at Certificate III level"),
	
	/*Certificate I and II level*/
	/**
	 * @PublicApi
	 */
	CERTIFICATE_2_LEVEL(521, "Certificate II", "Certificate II"),

	/**
	 * @PublicApi
	 */
	STATEMENT_OF_ATTEINMENT_AT_CERTIFICATE_2_LEVEL(522, "Statement of Attainment at Certificate II level",
		"Statement of Attainment at Certificate II level"),

	/**
	 * @PublicApi
	 */
	BRIDGING_AND_ENABLING_COURSE_AT_CERTIFICATE_2_LEVEL(523, "Bridging and Enabling Course at Certificate II level",
		"Bridging and Enabling Course at Certificate II level"),

	/**
	 * @PublicApi
	 */
	CERTIFICATE_1_LEVEL(524, "Certificate I", "Certificate I"),

	/**
	 * @PublicApi
	 */
	STATEMENT_OF_ATTEINMENT_AT_CERTIFICATE_1_LEVEL(525, "Statement of Attainment at Certificate I level",
		"Statement of Attainment at Certificate I level"),
	
	/*Other*/
	/**
	 * @PublicApi
	 */
	OTHER(999, "Other qualification", "Other qualification, not elsewhere classified");

	private String displayValue;
	private String description;
	private int value;

	private CreditLevel(int value, String displayValue, String description) {
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
