package ish.common.payable;

/**
 * Common interface which used for recalculation of amount owing only for enrollments in final statuses.
 */
public interface EnrolmentInterface {
	
	public boolean isInFinalStatus();
}
