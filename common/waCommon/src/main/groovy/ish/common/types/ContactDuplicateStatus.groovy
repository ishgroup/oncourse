package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.API


/**
 * A set of values
 */
@API
public enum ContactDuplicateStatus implements DisplayableExtendedEnumeration<Integer> {

	@API
	POSSIBLE_MATCH(0, "Possible match"),
	
	@API
	REJECTED_MATCH(1, "Rejected match"),
	@API
	IN_TRANSACTION(2, "In transaction"),
	@API
	PROCESSED(3, "Processed");

	private String displayName;
	private int value;

	private ContactDuplicateStatus(int value, String displayValue) {
		this.displayName = displayValue;
		this.value = value;
	}
	
	
	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public Integer getDatabaseValue() {
		return value;
	}
}
