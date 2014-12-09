package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * @PublicApi
 */
public enum ProductStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	ACTIVE(0, "Active"),
	
	/**
	 * @PublicApi
	 */
	CANCELLED(1, "Cancelled"),

	/**
	 * @PublicApi
	 */
	CREDITED(2, "Credited"),

	/**
	 * @PublicApi
	 */
	REDEEMED(3, "Redeemed"),
	/**
	 * Represents status when voucher is persisted to database but its payment is not yet successful.
	 * 
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
