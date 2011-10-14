package ish.oncourse.model;

import ish.common.types.CreditCardType;
import ish.common.util.DisplayableExtendedEnumeration;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public enum PaymentGatewayType implements Serializable, DisplayableExtendedEnumeration {

	/**
	 * "OFF" mode. The payments(and the enrolling) are disabled when the college
	 * payment gateway type is disabled.
	 * <br/>
	 * FIXME now the value is "null" but it is useless because when the db value
	 * is null, it don't use the {@link PaymentGatewayType} as value for
	 * property. but this type can use another int value later.
	 */
	DISABLED(null, "OFF"),

	/**
	 * "TEST" mode. Now in test mode the {@link CreditCardType#MASTERCARD} is
	 * success, the others are fail
	 */
	TEST(0, "Test gateway"),
	/**
	 * "ON" mode. Gateway for Payment Express(see
	 * willow-wo/ishframeworks/ISHPaymentExpress).
	 */
	PAYMENT_EXPRESS(2, "Payment Express");

	/**
	 * The list of gateway types for which the processing should be performed
	 * through https request. This defines "ON" gateway mode.
	 */
	public static final List<PaymentGatewayType> SECURED_TYPES = Arrays.asList(PAYMENT_EXPRESS);

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
