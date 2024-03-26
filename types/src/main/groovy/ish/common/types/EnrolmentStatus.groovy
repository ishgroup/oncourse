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
 * Enrolments pass through various states as the enrolment process is completed. These statuses are particularly important for communication with the onCourse website and the progression of the enrolment through checks for number of places remaining and having the payment processed.
 *
 * Do not change the state of enrolments directly since enrolment state has important ramifications throughout onCourse.
 * Changing state without also adjusting invoice lines, outcomes and other objects may create broken data relationships.
 *
 */
@API
public enum EnrolmentStatus implements DisplayableExtendedEnumeration<Integer> {

	// payment and invoice statuses
	/**
	 * Indicates an state of conflict with data integrity rules (e.g., between the website and onCourse). i.e., something that should never happen and needs resolution.
	 *
	 * Database value: -99
	 */
	CORRUPTED(-99, ""),

	/**
	 * Enrolments in NEW state will almost never be seen. Many of the older workflows have now been changed so enrolments in NEW state aren't even saved to the database until they progress to IN_TRANSACTION. You should not set enrolments to this state.
	 *
	 * Database value: 0
	 *
	 */
	@API
	NEW(0, "Not processed"),

	/**
	 * Indicates a payment or enrolment that was unable to retrieve a result from willow on first attempt (i.e., on post-persist from quick enrol) and is as such queued for later processing by a server-side thread.
	 *
	 * Database value: 1
	 */
	@API
	QUEUED(1, "Awaiting confirmation"),

	/**
	 * Indicates current processing of a payment. E.g., the user has agreed to pay and all details are valid. However, all payments with STATUS_QUEUED will move again to a state of STATUS_IN_TRANSACTION prior to attempting subsequent processing.
     * Enrolments in this state will be automatically picked up by server-sides processing threads and moved to a final state.
	 *
	 * Database value: 2
	 */
	@API
	IN_TRANSACTION(2, "In transaction"),

	/**
	 * Indicates successful and confirmed completion of a payment or enrolment.
	 *
	 * Database value: 3
	 */
	@API
	SUCCESS(3, "Active"),

	/**
	 * Indicates a failed response due to an error.
	 *
	 * Database value: 4
	 */
	@API
	FAILED(4, "Failed"),

	/**
	 * Indicates a failed response given by the credit card gateway.
	 *
	 * Database value: 6
	 */
	@API
	FAILED_CARD_DECLINED(6, "Card declined"),

	/**
	 * Indicates that the enrolment and payment could not be accepted because there were no enrolment places left.
	 *
	 * Database value: 7
	 */
	@API
	FAILED_NO_PLACES(7, "Failed - no places"),

	/**
	 * Indicates that an enrolment that was previously successful has been cancelled.
	 *
	 * Database value: 8
	 */
	@API
	CANCELLED(8, "Cancelled"),

	/**
	 * Indicates an equivalent status to that of cancelled but that a credit note was also created for the student in the system.
	 *
	 * Database value: 9
	 */
	@API
	REFUNDED(9, "Credited");

	
	public static final String STATUS_COMPLETE = 'Complete'

	/**
	 * The complete list of statuses that may be returned from the WillowServices soap gateway.
	 * Note: The list is made up of both transient and final statuses.
	 *
	 */
	@API
	public static final List<EnrolmentStatus> STATUSES_GATEWAY = Arrays.asList(CORRUPTED,
            QUEUED,
            SUCCESS,
            FAILED,
            FAILED_NO_PLACES);

	/**
	 * The list of statuses indicating a legitimate enrolment. i.e., those that indicate an 'existing' enrolment for a student, or that otherwise counts towards the number of places already taken in a class.
	 * The transient statuses, {@link PaymentStatus#IN_TRANSACTION}, {@link PaymentStatus#QUEUED} and {@link PaymentStatus#NEW}, are considered legitimate until either being set automatically to {@link PaymentStatus#SUCCESS} or to one of the {@link PaymentStatus#FAILED} statuses, by a gateway processing thread, so as to ensure that over enrolment should not occur.
	 * IMPORTANT: It should be considered unsafe to cancel an enrolment via the client gui if the status is transient unless you can guarantee that another thread is not already waiting a response from the gateway. That's a challenge that's hard to overcome in a three tier application.
	 * Note: The list is made up of both transient and final statuses.
	 *
	 */
	@API
	public static final List<EnrolmentStatus> STATUSES_LEGIT = Arrays.asList(NEW,
            QUEUED,
            IN_TRANSACTION,
            SUCCESS);

	/**
	 * The complete list of statuses that indicate non-success.
	 * Note: Each failed status is a final status so far as the runtime is concerned.
	 *
	 */
	@API
	public static final List<EnrolmentStatus> STATUSES_FAILED = Arrays.asList(FAILED,
            FAILED_NO_PLACES,
            FAILED_CARD_DECLINED,
            CORRUPTED);

	/**
	 * The list of statuses that indicate a prior enrolment of status {@link PaymentStatus#SUCCESS} into a class where the student has either cancelled and optionally been refunded their fees.
	 *
	 */
	@API
	public static final List<EnrolmentStatus> STATUSES_CANCELLATIONS = Arrays.asList(CANCELLED, REFUNDED);

	/**
	 * The complete list of statuses that are final or otherwise set in stone.
	 * Note: Corrupted status should of course, require investigation, as it has occurred due to a duplicate or conflicting record being found. Each of the other statuses is the result of a known and expected path of logic discounting bugs.
	 *
	 */
	@API
	public static final List<EnrolmentStatus> STATUSES_FINAL = Arrays.asList(SUCCESS,
            FAILED,
            FAILED_NO_PLACES,
            FAILED_CARD_DECLINED,
            CORRUPTED,
            CANCELLED,
            REFUNDED);

	private String displayName;
	private int value;

	private EnrolmentStatus(int value, String displayName) {
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
