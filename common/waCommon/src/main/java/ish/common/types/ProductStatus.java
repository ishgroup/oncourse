package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

public enum ProductStatus implements DisplayableExtendedEnumeration<Integer> {
	
	ACTIVE(0, "Active"),
	CANCELLED(1, "Cancelled"),
	CREDITED(2, "Credited"),
	REDEEMED(3, "Redeemed"),
	/**
	 * Represents status when voucher is persisted to database but its payment is not yet successful.
	 */
	NEW(4, "New"),
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
