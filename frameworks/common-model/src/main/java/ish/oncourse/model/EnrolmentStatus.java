/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public enum EnrolmentStatus implements Serializable, DisplayableExtendedEnumeration {
	// payment and invoice statuses
	/**
	 * Indicates an state of conflict with data integrity rules (e.g., between the website and onCourse). i.e., something that should never happen and needs
	 * resolution.
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
	 * gateway is disabled. Something to think about.
	 */
	NEW(0, "Not processed"), // FIXME: it would appear that the usage of this status is not consistent with its meaning.
	/**
	 * Indicates a payment or enrolment that was unable to retrieve a result from willow on first attempt (i.e., on post-persist from quick enrol) and is as
	 * such queued for later processing by a server-side thread.
	 */
	QUEUED(1, "Awaiting confirmation"),
	/** 
	 * Indicates current processing of a payment 
	 */
	IN_TRANSACTION(2, "In processing"),
	
	/**
	 * Indicates successful and confirmed completion of a payment or enrolment.
	 */
	SUCCESS(3, "Active"),
	/**
	 * Indicates a failed response due to an error.
	 */
	FAILED(4, "Failed"),
	/**
	 * Indicates that the enrolment and payment could not be accepted because there were no enrolment places left.
	 */
	FAILED_NO_PLACES(7, "Failed - no places"),
	/** 
	 * Indicates that the gateway failed the payment 
	 */
	FAILED_CARD_DECLINED( 6, "Failed card declined" ),
	
	/**
	 * Indicates that an enrolment that was previously successful has been cancelled due to the student pulling out.
	 */
	CANCELLED(8, "Cancelled"),
	/**
	 * Indicates an equivalent status to that of {@link #CANCELLED} but that a credit note was also created for the student in the system.
	 */
	REFUNDED(9, "Credited");

	/**
	 * The complete list of enrolment statuses known to onCourse.
	 * <p>
	 * <b>Note:</b> The list is made up of both transient and final statuses.
	 */
	public static final List<EnrolmentStatus> STATUSES_ALL = Arrays.asList(new EnrolmentStatus[] {
			CORRUPTED,
			NEW,
			QUEUED,
			IN_TRANSACTION,
			SUCCESS,
			FAILED,
			FAILED_CARD_DECLINED,
			FAILED_NO_PLACES,
			CANCELLED,
			REFUNDED });
	/**
	 * The complete list of statuses that may be returned from the WillowServices soap gateway.
	 * <p>
	 * <b>Note:</b> The list is made up of both transient and final statuses.
	 */
	public static final List<EnrolmentStatus> STATUSES_GATEWAY = Arrays.asList(new EnrolmentStatus[] {
			EnrolmentStatus.CORRUPTED,
			EnrolmentStatus.QUEUED,
			EnrolmentStatus.SUCCESS,
			EnrolmentStatus.FAILED,
			EnrolmentStatus.FAILED_NO_PLACES });

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
	 */
	public static final List<EnrolmentStatus> STATUSES_LEGIT = Arrays.asList(new EnrolmentStatus[] {
			EnrolmentStatus.NEW,
			EnrolmentStatus.QUEUED,
			EnrolmentStatus.SUCCESS,
			IN_TRANSACTION});
	/**
	 * The complete list of statuses that indicate non-success.
	 * <p>
	 * <b>Note:</b> Each failed status is a final status so far as the runtime is concerned.
	 */
	public static final List<EnrolmentStatus> STATUSES_FAILED = Arrays.asList(new EnrolmentStatus[] {
			EnrolmentStatus.FAILED,
			EnrolmentStatus.FAILED_NO_PLACES,
			FAILED_CARD_DECLINED,
			EnrolmentStatus.CORRUPTED });
	/**
	 * The list of statuses that indicate a prior enrolment of status {@link PaymentStatus#SUCCESS} into a class where the student has either cancelled and
	 * optionally been refunded their fees.
	 */
	public static final List<EnrolmentStatus> STATUSES_CANCELLATIONS = Arrays.asList(EnrolmentStatus.CANCELLED, EnrolmentStatus.REFUNDED);
	/**
	 * The complete list of statuses that are final or otherwise set in stone.
	 * <p>
	 * <b>Note:</b> Corrupted status should of course, require investigation, as it has occurred due to a duplicate or conflicting record being found. Each of
	 * the other statuses is the result of a known and expected path of logic discounting bugs.
	 */
	public static final List<EnrolmentStatus> STATUSES_FINAL = Arrays.asList(new EnrolmentStatus[] {
			EnrolmentStatus.SUCCESS,
			EnrolmentStatus.FAILED,
			EnrolmentStatus.FAILED_NO_PLACES,
			FAILED_CARD_DECLINED,
			EnrolmentStatus.CORRUPTED,
			EnrolmentStatus.CANCELLED,
			EnrolmentStatus.REFUNDED });

	private String displayName;
	private int value;

	private EnrolmentStatus(int value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	public Integer getDatabaseValue() {
		return this.value;
	}

	public String getDisplayName() {
		return displayName;
	}

	public static EnrolmentStatus getStatusForInt(Integer aValue) {
		if (aValue == null)
			return null;
		switch (aValue.intValue()) {
		case -99:
			return CORRUPTED;
		case 0:
			return NEW;
		case 1:
			return QUEUED;
		case 3:
			return SUCCESS;
		case 4:
			return FAILED;
		case 6:
			return SUCCESS;
		case 7:
			return FAILED_NO_PLACES;
		case 8:
			return CANCELLED;
		case 9:
			return REFUNDED;
		default:
			return null;
		}
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

	@Override
	public String toString() {
		return getDisplayName();
	}
}
