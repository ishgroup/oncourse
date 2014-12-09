package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * Voucher verification status enumeration should be marked by willow when processing voucher payments coming from 
 * angel and used in angel to display appropriate user message.
 * 
 * @PublicApi
 */
public enum VoucherPaymentStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	APPROVED(0, "Approved"),
	/**
	 * @PublicApi
	 */
	BUSY(1, "Busy"),

	/**
	 * @PublicApi
	 */
	INCONSISTENT(2, "Inconsistent");
	
	private int value;
	private String displayName;
	
	private VoucherPaymentStatus(int value, String displayName) {
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
