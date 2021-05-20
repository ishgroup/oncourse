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

package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.API
/**
 * Type of Field Configuration is condition under which it will be used.
 */
@API
enum FieldConfigurationType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 1
	 * Field Configuration will be used during web enrol process
	 */
	@API
	ENROLMENT(1, "Enrolment"),

	/**
	 * Database value: 2
	 * Field Configuration will be used during web application process
	 */
	@API
	APPLICATION(2, "Application"),

	/**
	 * Database value: 3
	 * Field Configuration will be used when joining waiting lists on web
	 */
	@API
	WAITING_LIST(3, "Waiting List"),

	/**
	 * Database value: 4
	 * Field Configuration will be used for surveys
	 */
	@API
	SURVEY(4, "Survey"),

	/**
	 * Database value: 5
	 * Field Configuration will be used for payers
	 */
	@API
	PAYER(5, "Payer"),

	/**
	 * Database value: 6
	 * Field Configuration will be used for parents or guardians
	 */
	@API
	PARENT(6, "Parent"),

	/**
	 * Database value: 7
	 * Field Configuration will be used for articles
	 */
	@API
	PRODUCT(7, "Product"),

	/**
	 * Database value: 8
	 * Field Configuration will be used for memberships
	 */
	@API
	MEMBERSHIP(8, "Membership"),

	/**
	 * Database value: 9
	 * Field Configuration will be used for vouchers
	 */
	@API
	VOUCHER(9, "Voucher")

	private String displayName
	private int value

	private FieldConfigurationType(int value, String displayName) {
		this.value = value
		this.displayName = displayName
	}

	@Override
	Integer getDatabaseValue() {
		return this.value
	}

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDisplayName()
	 */
	@Override
	String getDisplayName() {
		return this.displayName
	}
}
