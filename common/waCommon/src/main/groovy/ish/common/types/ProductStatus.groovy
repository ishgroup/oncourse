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
	 */
	@API
	ACTIVE(0, "Active"),
	
	/**
	 * A product sale which is cancelled but not reversed.
	 * 
	 * Database value: 1
	 */
	@API
	CANCELLED(1, "Cancelled"),

	/**
	 * When a product sale has been reversed.
	 * 
	 * Database value: 2
	 */
	@API
	CREDITED(2, "Credited"),

	/**
	 * A voucher which has been sold and also redeemed. Doesn't apply to other types of products.
	 * 
	 * Database value: 3
	 */
	@API
	REDEEMED(3, "Redeemed"),
	
	/**
	 * Represents status when voucher is persisted to database but its payment is not yet successful.
	 * This status will transition to another status soon. You should not set this status ordinarily.
	 * 
	 * Database value: 4
	 */
	@API
	NEW(4, "New"),

	/**
	 * Database value: 5
	 */
	@API
	EXPIRED(5, "Expired"),

	/**
	 * Database value: 6
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
