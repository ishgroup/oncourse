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
 * Products can have a status reflecting the sale. Since products include both real goods ({@see Articles}) and {@see Vouchers} some statuses only apply to specific types of products.
 */
@API
public enum ProductStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 0
	 *
	 * Base status product receives after payment was confirmed.
	 */
	@API
	ACTIVE(0, "Active"),

	/**
	 * Database value: 1
	 *
	 * A product sale which is cancelled but not reversed.
	 */
	@API
	CANCELLED(1, "Cancelled"),

	/**
	 * Database value: 2
	 *
	 * When a product sale has been reversed.
	 */
	@API
	CREDITED(2, "Credited"),

	/**
	 * Database value: 3
	 *
	 * A voucher which has been sold and also redeemed. Doesn't apply to other types of products.
	 */
	@API
	REDEEMED(3, "Redeemed"),

	/**
	 * Database value: 4
	 *
	 * Represents status when voucher is persisted to database but its payment is not yet successful.
	 * This status will transition to another status soon. You should not set this status ordinarily.
	 */
	@API
	NEW(4, "New"),

	/**
	 * Database value: 5
	 *
	 * Product is past it's expiry date
	 */
	@API
	EXPIRED(5, "Expired"),

	/**
	 * Database value: 6
	 *
	 * Status that can be set manually for Article Product to confirm it's delivery.
	 */
	@API
	DELIVERED(6, "Delivered");

	private int value;
	private String displayName;

	ProductStatus(int value, String displayName) {
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
