package ish.oncourse.utils;

import ish.common.types.EnrolmentStatus;

import static ish.common.types.EnrolmentStatus.*;

/**
 * Created by akoiro on 14/04/2016.
 */
public class EnrolmentStatusValidator {
	private EnrolmentStatus currentStatus;
	private EnrolmentStatus newStatus;

	public boolean validate() {
		if (currentStatus == newStatus) {
			return true;
		}
		switch (currentStatus) {
			case NEW:
				return true;
			case QUEUED:
				return newStatus != NEW;
			case IN_TRANSACTION:
				return newStatus != QUEUED && newStatus != NEW;
			case FAILED:
			case FAILED_CARD_DECLINED:
			case FAILED_NO_PLACES:
			case CANCELLED:
			case REFUNDED:
			case CORRUPTED:
				return false;
			case SUCCESS:
				return STATUSES_CANCELLATIONS.contains(newStatus);
			default:
				throw new IllegalArgumentException(String.format("Unsupported status '%s' found!", currentStatus));
		}
	}

	public static EnrolmentStatusValidator valueOf(EnrolmentStatus currentStatus, EnrolmentStatus newStatus) {
		EnrolmentStatusValidator validator = new EnrolmentStatusValidator();
		validator.currentStatus = currentStatus;
		validator.newStatus = newStatus;
		return validator;
	}
}
