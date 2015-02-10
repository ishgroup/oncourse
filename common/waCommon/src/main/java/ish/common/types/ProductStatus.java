package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * Products can have a status reflecting the sale. Since products include both real goods ({@see Articles}) and {@see Vouchers} some statuses only apply to specific types of products.
 * @PublicApi
 */
public enum ProductStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 0
	 * @PublicApi
	 */
	ACTIVE(0, "Active"),
	
	/**
	 * A product sale which is cancelled but not reversed.
	 * 
	 * Database value: 1
	 * @PublicApi
	 */
	CANCELLED(1, "Cancelled"),

	/**
	 * When a product sale has been reversed.
	 * 
	 * Database value: 2
	 * @PublicApi
	 */
	CREDITED(2, "Credited"),

	/**
	 * A voucher which has been sold and also redeemed. Doesn't apply to other types of products.
	 * 
	 * Database value: 3
	 * @PublicApi
	 */
	REDEEMED(3, "Redeemed"),
	
	/**
	 * Represents status when voucher is persisted to database but its payment is not yet successful.
	 * This status will transition to another status soon. You should not set this status ordinarily.
	 * 
	 * Database value: 4
	 * @PublicApi
	 */
	NEW(4, "New"),

	/**
	 * @PublicApi
	 */
	EXPIRED(5, "Expired");
	
	private int value;
	private String displayName;
	
	private ProductStatus(int value, String displayName) {
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
