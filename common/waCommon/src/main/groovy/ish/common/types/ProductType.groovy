/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * Products are implemented in the database using vertical inheritance and require this discriminator column
 * to identify the type of entity.
 * 
 */
@API
public enum ProductType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 1
	 *
	 * Product items offered by college, e.g, books.
	 * Located in Products -> Products.
	 */
	@API
	ARTICLE(1, "Product"),

	/**
	 * Database value: 2
	 *
	 * Memberships offered by college.
	 * Located in Products -> Memberships.
	 */
	@API
	MEMBERSHIP(2, "Membership"),

	/**
	 * Database value: 3
	 *
	 * Money and enrolment vouchers sold by college. Voucher can be used to cover the payments for enrolling into classes.
	 * Located in Products -> Voucher types.
	 */
	@API
	VOUCHER(3, "Voucher");

	private String displayName;
	private int value;

	private ProductType(int value, String displayName) {
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
}
