/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import org.apache.cayenne.map.LifecycleEvent;

import java.util.Arrays;
import java.util.List;

/**
 * Scripts can be triggered on one of these events from any Cayenne entity.
 */
public enum EntityEvent implements DisplayableExtendedEnumeration<Integer> {

	CREATE(1, "Create", LifecycleEvent.POST_PERSIST),
	UPDATE(2, "Update", LifecycleEvent.POST_UPDATE),
	REMOVE(3, "Remove", LifecycleEvent.PRE_REMOVE),
	CREATE_OR_UPDATE(4, "Create or update", LifecycleEvent.POST_PERSIST, LifecycleEvent.POST_UPDATE);

	private int value;
	private String displayName;
	private List<LifecycleEvent> lifecycleEvent;

	private EntityEvent(int value, String displayName, LifecycleEvent... lifecycleEvent) {
		this.value = value;
		this.displayName = displayName;
		this.lifecycleEvent = Arrays.asList(lifecycleEvent);
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public Integer getDatabaseValue() {
		return value;
	}

	public List<LifecycleEvent> getLifecycleEvents() {
		return lifecycleEvent;
	}
}
