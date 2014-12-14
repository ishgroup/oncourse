/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * Products are implemented in the database using vertical inheritance and require this discriminator column
 * to identify the type of entity.
 * 
 * @PublicApi
 */
public enum ProductType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	ARTICLE(1, "Product"),

	/**
	 * @PublicApi
	 */
	MEMBERSHIP(2, "Membership"),

	/**
	 * @PublicApi
	 */
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
