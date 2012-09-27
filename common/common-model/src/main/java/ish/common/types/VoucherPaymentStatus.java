package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * Temporary copy of the angel waCommon VoucherPaymentStatus. Need to replaced after the angel will be able to release.
 * @author vdavidovich
 *
 */
public enum VoucherPaymentStatus implements DisplayableExtendedEnumeration<Integer> {
	
	APPROVED(0, "Approved"),
	BUSY(1, "Busy"),
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
