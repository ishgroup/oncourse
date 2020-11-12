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

package ish.print;

import ish.oncourse.API;

/**
 * Enum determining the strategy for adding page breaks between records in reports.
 */
@API
public enum PageBreakType {

	/**
	 * No automatic page breaks.
	 */
	@API
	OFF("false"),

	/**
	 * Page breaks are added after every printed record after transformation.
	 */
	@API
	TARGET("true"),

	/**
	 * Page breaks are added after printing list of records which are belong to single source record.
	 */
	@API
	SOURCE("source");

	private String alias;

	PageBreakType(String alias) {
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	/**
	 * Get enum value for alias string.
	 *
	 * @param alias
	 * @return enum value
	 */
	public static PageBreakType getByAlias(String alias) {
		for (PageBreakType type : values()) {
			if (type.getAlias().equals(alias)) {
				return type;
			}
		}

		return null;
	}
}
