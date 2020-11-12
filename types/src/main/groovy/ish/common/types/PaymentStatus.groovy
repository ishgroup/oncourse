/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.common.types
import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.API
/**
 * Payments pass through a number of statuses as the enrolment or sale is completed and the credit card is processed.
 * Once a final state is reached, the status may not be changed again.
 *
 */
@API
public enum PaymentStatus implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Indicates an state of conflict with data integrity rules (e.g., between the website and onCourse).
     * i.e., something that should never happen and needs resolution.
     *
     * Database value: -99
	 *
	 */
	CORRUPTED(-99, ""),

	/**
	 * NEW payments are rarely seen in the wild and exist for a short time before the gateway is contacted to process
     * the transaction.
	 *
     * Database value: 0
	 */
	@API
	NEW(0, "Not processed"), // FIXME: it would appear that the usage of this
								// status is not consistent with its meaning.

	/**
	 * Indicates a payment or enrolment that was unable to retrieve a result on first attempt and is
	 * queued for later processing by a server-side thread.
	 *
     * Database value: 1
	 */
	@API
	QUEUED(1, "Queued"),

	/**
	 * This payment is currently being processed against the gateway and should not be touched by any other thread.
	 *
     * Database value: 2
	 */
	@API
	IN_TRANSACTION(2, "In transaction"),

	/**
	 * Indicates successful and confirmed completion of a payment or enrolment.
	 *
     * FINAL STATUS
     * Database value: 3
	 */
	@API
	SUCCESS(3, "Success"),

	/**
	 * Indicates a failed response due to an error.
	 *
     * FINAL STATUS
     * Database value: 4
	 */
	@API
	FAILED(4, "Failed"),

	/**
	 * Indicates a failed response given by the credit card gateway.
	 *
     * FINAL STATUS
     * Database value: 6
	 */
	@API
	FAILED_CARD_DECLINED(6, "Card declined"),

	/**
	 * Indicates that the enrolment and payment could not be accepted because there were no enrolment places left.
	 *
     * FINAL STATUS
     * Database value: 7
	 */
	@API
	FAILED_NO_PLACES(7, "Rejected - no places available"),

	/**
	 * Indicates that payment was saved in onCourse Web, but user needs to provide credit card details.
	 *
     * Database value: 10
	 */
	@API
	CARD_DETAILS_REQUIRED(10, "Credit card details required");

	/**
	 * The complete list of statuses that may be returned from the WillowServices soap gateway.
	 *
	 * Note: The list is made up of both transient and final statuses.
	 *
	 */
	public static final List<PaymentStatus> STATUSES_GATEWAY = Arrays.asList(PaymentStatus.CORRUPTED,
            PaymentStatus.QUEUED,
            PaymentStatus.SUCCESS,
            PaymentStatus.FAILED,
            PaymentStatus.FAILED_CARD_DECLINED,
            PaymentStatus.FAILED_NO_PLACES);

	/**
	 * The list of statuses indicating a legitimate enrolment. i.e., those that indicate an 'existing' enrolment
     * for a student, or that otherwise counts towards the number of places already taken in a class.
	 *
	 * The transient statuses, {@link PaymentStatus#QUEUED} and {@link PaymentStatus#NEW}, are considered legitimate until either being set automatically to
	 * {@link PaymentStatus#SUCCESS} or to one of the failed statuses, by a gateway processing thread,
     * so as to ensure that over enrolment should not occur.
	 *
	 * IMPORTANT: It should be considered unsafe to cancel an enrolment via the client gui if the status is transient
     * unless you can guarantee that another thread is not already waiting a response from the gateway.
     * That's a challenge that's hard to overcome in a three tier application.
	 *
	 * Note: The list is made up of both transient and final statuses.
	 *
	 */
	@API
	public static final List<PaymentStatus> STATUSES_LEGIT = Arrays
			.asList(PaymentStatus.NEW, PaymentStatus.QUEUED, PaymentStatus.SUCCESS);
	/**
	 * The complete list of statuses that indicate non-success.
	 *
	 * Note: Each failed status is a final status so far as the runtime is concerned.
	 *
	 */
	@API
	public static final List<PaymentStatus> STATUSES_FAILED = Arrays.asList(PaymentStatus.FAILED,
            PaymentStatus.FAILED_CARD_DECLINED,
            PaymentStatus.FAILED_NO_PLACES,
            PaymentStatus.CORRUPTED);
	/**
	 * The complete list of statuses that are final and cannot be modified.
	 *
	 */
	@API
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
	@API
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
