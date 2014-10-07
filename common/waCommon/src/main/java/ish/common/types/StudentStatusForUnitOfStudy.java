package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * A code which indicates the student status for a unit of study
 * http://heimshelp.deewr.gov.au/sites/heimshelp/2014_data_requirements/2014dataelements/pages/490
 * @author vdavidovich
 */
public enum StudentStatusForUnitOfStudy implements DisplayableExtendedEnumeration<Integer> {

	/*VET Students only:*/
	/*VET FEE-HELP assisted students*/
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_NON_STATE_GOVERNMENT_SUBSIDISED(401,
		"non State Government subsidised (01)",
			"Deferred all/part of tuition fee through VET FEE-HELP - non State Government subsidised"),
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_RESTRICTED_ACCESS_ARRANGEMENT(402,
		"Restricted Access Arrangement (02)",
			"Deferred all/part of tuition fee through VET FEE-HELP - Restricted Access Arrangement"),
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_VICTORIAN_STATE_GOVERNMENT_SUBSIDISED(403,
		"Victorian State Government subsidised (03)",
			"Deferred all/part of tuition fee through VET FEE-HELP - Victorian State Government subsidised"),
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_NEW_SOUTH_WALES_STATE_GOVERNMENT_SUBSIDISED(404,
			"New South Wales State Government subsidised (04)", 
			"Deferred all/part of tuition fee through VET FEE-HELP - New South Wales State Government subsidised"),
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_QUEENSLAND_STATE_GOVERNMENT_SUBSIDISED(405,
		"Queensland State Government subsidised (05)",
			"Deferred all/part of tuition fee through VET FEE-HELP - Queensland State Government subsidised"),
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_SOUTH_AUSTRALIAN_STATE_GOVERNMENT_SUBSIDISED(406,
		"South Australian State Government subsidised (06)",
			"Deferred all/part of tuition fee through VET FEE-HELP - South Australian State Government subsidised"),
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_WESTERN_AUSTRALIAN_STATE_GOVERNMENT_SUBSIDISED(407,
		"Western Australian State Government subsidised (07)",
			"Deferred all/part of tuition fee through VET FEE-HELP - Western Australian State Government subsidised"),	
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_TASMANIA_STATE_GOVERNMENT_SUBSIDISED(408,
			"Tasmania State Government subsidised (08)",
			"Deferred all/part of tuition fee through VET FEE-HELP - Tasmania State Government subsidised"),
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_NORTHERN_TERRITORY_GOVERNMENT_SUBSIDISED(409,
			"Northern Territory Government subsidised (09)",
			"Deferred all/part of tuition fee through VET FEE-HELP - Northern Territory Government subsidised"),
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_AUSTRALIAN_CAPITAL_TERRITORY_GOVERNMENT_SUBSIDISED(410, 
			"Australian Capital Territory Government subsidised (10)",
			"Deferred all/part of tuition fee through VET FEE-HELP - Australian Capital Territory Government subsidised");

	private String displayValue;
	private String description;
	private int value;

	private StudentStatusForUnitOfStudy(int value, String displayValue, String description) {
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
