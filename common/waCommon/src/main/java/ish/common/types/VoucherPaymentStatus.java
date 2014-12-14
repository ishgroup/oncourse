package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * Voucher verification status enumeration are set by onCourse Web when processing voucher payments coming from 
 * onCourse and used to display appropriate user message.
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
