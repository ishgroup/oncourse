/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

import java.util.Arrays;
import java.util.List;

/**
 * enumeration of available payment statuses
 * 
 * @PublicApi
 */
public enum PaymentStatus implements DisplayableExtendedEnumeration<Integer> {
	
	// payment and invoice statuses
	/**
	 * Indicates an state of conflict with data integrity rules (e.g., between the website and onCourse). i.e., something that should never happen and needs
	 * resolution.
	 * 
	 * @PublicApi
	 */
	CORRUPTED(-99, ""),

	/**
	 * Previous comment said 'when the ish payment gateway is not used' which sounds a little confusing as to why it's called 'new'. Either way, that's blurring
	 * the concepts with what 'type' is used to determine.
	 * <p>
	 * Suggested comment: 'Indicates an as yet unconfirmed payment or enrolment.'
	 * <p>
	 * Suggested logic: On server startup, any payments or enrolments left in this state (due to a crash of some kind) can be set to {@link #QUEUED} so as to
	 * ensure that willow can be contacted again, if we crashed before the response, to obtain the result, or to contact willow for the first time if it never
	 * got that far before a crash. This would seem to remove the need for a null status which is not one of the official statuses.
	 * <p>
	 * Otherwise perhaps it'd make sense to for new payments/enrolments to always be committed from the client with a status of new thus allowing the
	 * server-side post-persist to update the status to success according to the logic of whether willow returned success, or willow is not involved, or if the
	 * gateway is disabled. Something to think about. Used by angel and willow
	 * 
	 * @PublicApi
	 */
	NEW(0, "Not processed"), // FIXME: it would appear that the usage of this
								// status is not consistent with its meaning.

	/**
	 * Indicates a payment or enrolment that was unable to retrieve a result from willow on first attempt (i.e., on post-persist from quick enrol) and is as
	 * such queued for later processing by a server-side thread. Used by angel and willow
	 * 
	 * @PublicApi
	 */
	QUEUED(1, "Queued"),

	/**
	 * Indicates current processing of a payment. E.g., the user has agreed to pay and all details are valid. However, all payments with STATUS_QUEUED will move
	 * again to a state of STATUS_IN_TRANSACTION prior to attempting subsequent processing. It has this definite meaning: We are about to process the payment by
	 * contacting the payment gateway. Used by willow
	 * 
	 * @PublicApi
	 */
	IN_TRANSACTION(2, "In transaction"),

	/**
	 * Indicates successful and confirmed completion of a payment or enrolment. Used by angel and willow
	 * 
	 * @PublicApi
	 */
	SUCCESS(3, "Success"),

	/**
	 * Indicates a failed response due to an error. Used by angel and willow
	 * 
	 * @PublicApi
	 */
	FAILED(4, "Failed"),

	/**
	 * Indicates a failed response given by the credit card gateway. Used by angel and willow
	 * 
	 * @PublicApi
	 */
	FAILED_CARD_DECLINED(6, "Card declined"),

	/**
	 * Indicates that the enrolment and payment could not be accepted because there were no enrolment places left. Used by angel and willow
	 * 
	 * @PublicApi
	 */
	FAILED_NO_PLACES(7, "Rejected - no places available"),

	/**
	 * Indicates that payment was saved on willow side, but user needs to provide credit card details.
	 * 
	 * @PublicApi
	 */
	@Deprecated //we don't use the status anymore
	CARD_DETAILS_REQUIRED(10, "Credit card details required");

	/**
	 * The complete list of statuses that may be returned from the WillowServices soap gateway.
	 * <p>
	 * <b>Note:</b> The list is made up of both transient and final statuses.
	 * 
	 * @PublicApi
	 */
	public static final List<PaymentStatus> STATUSES_GATEWAY = Arrays.asList(PaymentStatus.CORRUPTED,
            PaymentStatus.QUEUED,
            PaymentStatus.SUCCESS,
            PaymentStatus.FAILED,
            PaymentStatus.FAILED_CARD_DECLINED,
            PaymentStatus.FAILED_NO_PLACES);

	/**
	 * The list of statuses indicating a legitimate enrolment. i.e., those that indicate an 'existing' enrolment for a student, or that otherwise counts towards
	 * the number of places already taken in a class.
	 * <p>
	 * The transient statuses, {@link PaymentStatus#QUEUED} and {@link PaymentStatus#NEW}, are considered legitimate until either being set automatically to
	 * {@link PaymentStatus#SUCCESS} or to one of the {@link PaymentStatus#FAILED} statuses, by a gateway processing thread, so as to ensure that over enrolment
	 * should not occur.
	 * <p>
	 * <b>IMPORTANT:</b> It should be considered unsafe to cancel an enrolment via the client gui if the status is transient unless you can guarantee that
	 * another thread is not already waiting a response from the gateway. That's a challenge that's hard to overcome in a three tier application.
	 * <p>
	 * <b>Note:</b> The list is made up of both transient and final statuses.
	 * 
	 * @PublicApi
	 */
	public static final List<PaymentStatus> STATUSES_LEGIT = Arrays
			.asList(PaymentStatus.NEW, PaymentStatus.QUEUED, PaymentStatus.SUCCESS);
	/**
	 * The complete list of statuses that indicate non-success.
	 * <p>
	 * <b>Note:</b> Each failed status is a final status so far as the runtime is concerned.
	 * 
	 * @PublicApi
	 */
	public static final List<PaymentStatus> STATUSES_FAILED = Arrays.asList(PaymentStatus.FAILED,
            PaymentStatus.FAILED_CARD_DECLINED,
            PaymentStatus.FAILED_NO_PLACES,
            PaymentStatus.CORRUPTED);
	/**
	 * The complete list of statuses that are final or otherwise set in stone.
	 * <p>
	 * <b>Note:</b> Corrupted status should of course, require investigation, as it has occurred due to a duplicate or conflicting record being found. Each of
	 * the other statuses is the result of a known and expected path of logic discounting bugs.
	 * 
	 * @PublicApi
	 */
	public static final List<PaymentStatus> STATUSES_FINAL = Arrays.asList(PaymentStatus.SUCCESS,
            PaymentStatus.FAILED,
            PaymentStatus.FAILED_CARD_DECLINED,
            PaymentStatus.FAILED_NO_PLACES,
            PaymentStatus.CORRUPTED);

	private String displayValue;
	private int value;

	private PaymentStatus(int value, String displayValue) {
		this.value = value;
		this.displayValue = displayValue;
	}

	@Override
	public Integer getDatabaseValue() {
		return this.value;
	}

	@Override
	public String getDisplayName() {
		return this.displayValue;
	}

	/**
	 * checks if a given status is a final status (not to be altered anymore)
	 * 
	 * @param status to be verified
	 * @return true if the param belongs to statuses_final
	 */
	public static boolean isFinalState(PaymentStatus status) {
		return STATUSES_FINAL.contains(status);
	}

	@Override
	public String toString() {
		return getDisplayName();
	}

	public static boolean isExpired(PaymentStatus status, String privateNotes) {
		return PaymentStatus.STATUSES_FAILED.contains(status) && PAYMENT_EXPIRED_BY_TIMEOUT_MESSAGE.equals(privateNotes);
	}

	public static final String PAYMENT_EXPIRED_BY_TIMEOUT_MESSAGE = "Payment was expired.";
}
