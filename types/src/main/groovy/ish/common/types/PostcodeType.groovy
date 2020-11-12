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
import ish.oncourse.API;

/**
 * Australian postcodes range
 */
@API
public enum PostcodeType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Delivery Area
	 * Database value: 1
	 *
	 */
	@API
	DELIVERY_AREA(1, "Delivery Area"),

	/**
	 * Post Office Boxes. These types of postcodes cannot be used for AVETMISS reporting.
	 * Database value: 2
	 *
	 */
	@API
	POST_OFFICE_BOXES(2, "Post Office Boxes"),

	/**
	 * Large Volume Receiver
	 * Database value: 3
	 *
	 */
	@API
	LARGE_VOLUME_RECEIVER(3, "Large Volume Receiver");


	private int value;
	private String displayName;

	PostcodeType(int value, String displayName) {
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
