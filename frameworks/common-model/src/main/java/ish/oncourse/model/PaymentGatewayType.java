package ish.oncourse.model;

import ish.common.util.DisplayableExtendedEnumeration;

import java.io.Serializable;

public enum PaymentGatewayType implements Serializable, DisplayableExtendedEnumeration{

	DISABLED(null, "Test gateway"),
	PAYMENT_EXPRESS(2, "Payment Express");

	private PaymentGatewayType(Integer value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}
	
	private Integer value;
	private String displayName;
	
	public Integer getDatabaseValue() {
		return value;
	}

	public String getDisplayName() {
		return displayName;
	}
	
}
