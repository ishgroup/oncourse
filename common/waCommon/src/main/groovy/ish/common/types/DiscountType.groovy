/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * Every discount can be one of three different types.
 */
@API
public enum DiscountType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Discount amount will be calculated based on the percent of the original price. 
     * The value will be rounded according to the discount round property.
	 * Discount min and max value can be also applied when the discount has defined them.
	 * 
	 * Database value: 1
	 */
	@API
	PERCENT(1, "Percent"),
	
	/**
	 * Discount amount will be a set dollar value off the full price.
     * This value applies to the original price before tax.
	 * Tax value will be calculated after the discounted price is calculated.
	 * 
	 * Database value: 2
	 */
	@API
	DOLLAR(2, "Dollar"),
	
	/**
	 * Discounts of this type override the original price (before tax) of the class.
	 * Tax, if applicable, will be added to the defined dollar value.
	 * 
	 * Database value: 3
	 */
	@API
	FEE_OVERRIDE(3, "Fee override");

	private String displayName;
	private int value;

	private DiscountType(int value, String displayName) {
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
