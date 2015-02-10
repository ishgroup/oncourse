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
	 * Database value: 0
	 * @PublicApi
	 */
	APPROVED(0, "Approved"),
	
	/**
	 * Database value: 1
	 * @PublicApi
	 */
	BUSY(1, "Busy"),

	/**
	 * Database value: 2
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
