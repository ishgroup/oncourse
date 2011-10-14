package ish.oncourse.model;

import ish.common.util.DisplayableExtendedEnumeration;

import java.io.Serializable;

/**
 * Indicates the status of Enrolment. {@see Enrolment#getStatus()}
 * 
 * @author ksenia
 * 
 */
public enum EnrolmentStatus implements Serializable, DisplayableExtendedEnumeration {
	/**
	 * Default status. Set on enrolment creation. Indicates that enrolment is
	 * ready for transaction performing.
	 * 
	 * Also changes the {@link EnrolmentStatus#IN_TRANSACTION} if the payment is
	 * failed during transaction.
	 */
	PENDING("Pending", "Pending"),
	/**
	 * Indicates current processing of an enrolment's payment. E.g., the user
	 * has agreed to pay and all details are valid, gateway processing is
	 * started. If the payment is failed during the gateway processing, status
	 * is changed to {@link EnrolmentStatus#PENDING}
	 */
	IN_TRANSACTION("In Transaction", "In Transaction"),
	/**
	 * Indicates successful completion of an enrolment's payment.
	 */
	SUCCESS("Success", "Success"),
	/**
	 * Indicates a failed enrolment. It is set when the Session times out or is
	 * otherwise ended
	 */
	FAILED("Failed", "Failed"),
	/**
	 * Indicates that an enrolment that was previously successful has been
	 * cancelled due to the student pulling out.
	 */
	CANCELLED("Cancelled", "Cancelled"),
	
	REFUNDED("Refunded", "Credited");

	/**
	 * Display name for the item.
	 */
	private String displayName;

	/**
	 * The value stored in db.
	 */
	private String databaseValue;

	/**
	 * Statuses for which the class place is considered to be occupied.
	 */
	public static EnrolmentStatus[] VALID_ENROLMENTS = new EnrolmentStatus[] { IN_TRANSACTION,
			SUCCESS };

	private EnrolmentStatus(String databaseValue, String displayName) {
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
	
	public static EnrolmentStatus getEnumForDatabaseValue(Object aValue) {
		if (aValue == null)
			return null;

		for (EnrolmentStatus ac : values()) {
			if (ac.getDatabaseValue().equals(aValue))
				return ac;
		}
		return null;
	}
}
