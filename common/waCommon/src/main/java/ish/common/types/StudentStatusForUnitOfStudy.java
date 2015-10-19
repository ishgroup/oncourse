package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * A code which indicates the student status for a unit of study
 * http://heimshelp.deewr.gov.au/sites/heimshelp/2014_data_requirements/2014dataelements/pages/490
 * 
 */
@API
public enum StudentStatusForUnitOfStudy implements DisplayableExtendedEnumeration<Integer> {

	/*VET Students only:*/
	/*VET FEE-HELP assisted students*/
	/**
	 * Database value: 1
	 */
	@API
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_NON_STATE_GOVERNMENT_SUBSIDISED(1,
		"non State Government subsidised",
			"Deferred all/part of tuition fee through VET FEE-HELP - non State Government subsidised"),

	/**
	 * Database value: 2
	 */
	@API
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_RESTRICTED_ACCESS_ARRANGEMENT(2,
		"Restricted Access Arrangement",
			"Deferred all/part of tuition fee through VET FEE-HELP - Restricted Access Arrangement"),

	/**
	 * Database value: 3
	 */
	@API
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_VICTORIAN_STATE_GOVERNMENT_SUBSIDISED(3,
		"Victorian State Government subsidised",
			"Deferred all/part of tuition fee through VET FEE-HELP - Victorian State Government subsidised"),

	/**
	 * Database value: 4
	 */
	@API
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_NEW_SOUTH_WALES_STATE_GOVERNMENT_SUBSIDISED(4,
			"New South Wales State Government subsidised", 
			"Deferred all/part of tuition fee through VET FEE-HELP - New South Wales State Government subsidised"),

	/**
	 * Database value: 5
	 */
	@API
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_QUEENSLAND_STATE_GOVERNMENT_SUBSIDISED(5,
		"Queensland State Government subsidised",
			"Deferred all/part of tuition fee through VET FEE-HELP - Queensland State Government subsidised"),

	/**
	 * Database value: 6
	 */
	@API
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_SOUTH_AUSTRALIAN_STATE_GOVERNMENT_SUBSIDISED(6,
		"South Australian State Government subsidised",
			"Deferred all/part of tuition fee through VET FEE-HELP - South Australian State Government subsidised"),

	/**
	 * Database value: 7
	 */
	@API
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_WESTERN_AUSTRALIAN_STATE_GOVERNMENT_SUBSIDISED(7,
		"Western Australian State Government subsidised",
			"Deferred all/part of tuition fee through VET FEE-HELP - Western Australian State Government subsidised"),	
	
	/**
	 * Database value: 8
	 */
	@API
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_TASMANIA_STATE_GOVERNMENT_SUBSIDISED(8,
			"Tasmania State Government subsidised",
			"Deferred all/part of tuition fee through VET FEE-HELP - Tasmania State Government subsidised"),
	
	/**
	 * Database value: 9
	 */
	@API
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_NORTHERN_TERRITORY_GOVERNMENT_SUBSIDISED(9,
			"Northern Territory Government subsidised",
			"Deferred all/part of tuition fee through VET FEE-HELP - Northern Territory Government subsidised"),

	/**
	 * Database value: 10
	 */
	@API
	DEFERRED_ALL_OR_PART_OF_TUITION_FEE_THROUGH_VET_FEE_HELP_AUSTRALIAN_CAPITAL_TERRITORY_GOVERNMENT_SUBSIDISED(10, 
			"Australian Capital Territory Government subsidised",
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
