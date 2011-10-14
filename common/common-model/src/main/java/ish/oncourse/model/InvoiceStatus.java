package ish.oncourse.model;

import ish.common.util.DisplayableExtendedEnumeration;

import java.io.Serializable;

/**
 * Indicates the status of Invoice. {@see Invoice#getStatus()}
 * 
 * @author ksenia
 * 
 */
public enum InvoiceStatus implements Serializable,
		DisplayableExtendedEnumeration {
	/**
	 * Default status. Set on invoice creation. Indicates that invoice is ready
	 * for transaction performing.
	 * 
	 * Also changes the {@link InvoiceStatus#IN_TRANSACTION} if the payment is
	 * failed during transaction.
	 */
	PENDING("Pending", "Pending"),
	/**
	 * Indicates current processing of an invoice's payment. E.g., the user has
	 * agreed to pay and all details are valid, gateway processing is started.
	 * If the payment is failed during the gateway processing, status is changed
	 * to {@link InvoiceStatus#PENDING}
	 */
	IN_TRANSACTION("In Transaction", "In Transaction"),
	/**
	 * Indicates successful completion of an invoice's payment.
	 */
	SUCCESS("Success", "Success"),
	/**
	 * Indicates a failed invoice. It is set when the Session times out or is
	 * otherwise ended
	 */
	FAILED("Failed", "Failed");

	/**
	 * Display name for the item.
	 */
	private String displayName;

	/**
	 * The value stored in db.
	 */
	private String databaseValue;

	private InvoiceStatus(String databaseValue, String displayName) {
		this.databaseValue = databaseValue;
		this.displayName = displayName;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.apache.cayenne.ExtendedEnumeration#getDatabaseValue()
	 */
	public Object getDatabaseValue() {
		return databaseValue;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDisplayName()
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	public static InvoiceStatus getSourceForValue(String value) {
		for (InvoiceStatus source : values()) {
			if (source.getDatabaseValue().equals(value)) {
				return source;
			}
		}
		return null;
	}
}
