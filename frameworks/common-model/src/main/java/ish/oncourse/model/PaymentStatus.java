package ish.oncourse.model;

import ish.common.util.DisplayableExtendedEnumeration;

import java.io.Serializable;
/**
 * Indicates the status of Payment. {@see PaymentIn#getStatus()} 
 * @author ksenia
 *
 */
public enum PaymentStatus implements Serializable, DisplayableExtendedEnumeration {
	/**
	 * Default status. Set on payment creation. Indicates that payment is ready
	 * for transaction performing.
	 */
	PENDING("Pending", "Pending"),
	/**
	 * Indicates current processing of a payment. E.g., the user has agreed to
	 * pay and all details are valid, gateway processing is started.
	 */
	IN_TRANSACTION("In transaction", "In transaction"),
	/**
	 * Indicates successful completion of a payment.
	 */
	SUCCESS("Success", "Success"),
	/**
	 * Indicates a failed payment.
	 */
	FAILED("Failed", "Failed"),
	/**
	 * Indicates that the payment is canceled and funds have been refunded.
	 */
	REFUNDED("Refunded", "Refunded");
	
	/**
	 * Display name for the item.
	 */
	private String displayName;
	
	/**
	 * The value stored in db.
	 */
	private String databaseValue;

	private PaymentStatus(String databaseValue, String displayName) {
		this.databaseValue = databaseValue;
		this.displayName = displayName;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * @see org.apache.cayenne.ExtendedEnumeration#getDatabaseValue()
	 */
	public Object getDatabaseValue() {
		return databaseValue;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDisplayName()
	 */
	public String getDisplayName() {
		return displayName;
	}

}
