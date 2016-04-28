package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * Voucher verification status enumeration are set by onCourse Web when processing voucher payments coming from 
 * onCourse and used to display appropriate user message.
 * 
 */
@API
public enum VoucherPaymentStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 0
	 *
	 * Voucher is valid and can be used for enrolling/payment.
	 */
	@API
	APPROVED(0, "Approved"),
	
	/**
	 * Database value: 1
	 *
	 * Voucher has already being used in some other transaction.
	 */
	@API
	BUSY(1, "Busy"),

	/**
	 * Database value: 2
	 *
	 * Voucher details are different on angel and willow.
	 */
	@API
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
