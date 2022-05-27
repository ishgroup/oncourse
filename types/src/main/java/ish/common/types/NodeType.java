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


public enum NodeType implements DisplayableExtendedEnumeration<Integer> {

	TAG(1, "Tag"),
	CHECKLIST(2, "Checklist");

	private String displayName;
	private int value;

	private NodeType(int value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	@Override
	public Integer getDatabaseValue() {
		return this.value;
	}

	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}

	public static NodeType fromDisplayName(String text) {
		for (NodeType b : NodeType.values()) {
			if (String.valueOf(b.displayName).equals(text)) {
				return b;
			}
		}
		return null;
	}
}
