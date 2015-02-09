/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

public enum TriggerType implements DisplayableExtendedEnumeration<Integer> {
	
	CRON(1, "Cron"),
	ENTITY_EVENT(2, "Entity event");

	private int value;
	private String displayName;

	private TriggerType(int value, String displayName) {
		this.value = value;
		this.displayName = displayName;
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
