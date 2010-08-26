package ish.oncourse.model;

import java.util.Arrays;
import java.util.List;

public interface ISHPayment {
	/** a final status indicating that the payment was cancelled */
	public static final Integer STATUS_CANCELLED = new Integer(8);
	/** a final status that cannot change again */
	public static final Integer STATUS_FAILED = new Integer(4);
	/** a final status that indicating that the gateway failed the payment */
	public static final Integer STATUS_FAILED_CARD_DECLINED = new Integer(6);
	/** a final status indicating that enrolment places could not be reserved */
	public static final Integer STATUS_FAILED_NO_PLACES = new Integer(7);
	/** a status that indicates current processing of a payment */
	public static final Integer STATUS_IN_TRANSACTION = new Integer(2);
	/** status indicates a new payment that has not been processed */
	public static final Integer STATUS_NEW = new Integer(0);
	/** a status that indicates the payment is queued for later (re)processing */
	public static final Integer STATUS_QUEUED = new Integer(1);
	/** a final status indicating cancelled and funds have been refunded */
	public static final Integer STATUS_REFUNDED = new Integer(9);
	/** a final status that cannot change again */
	public static final Integer STATUS_SUCCEEDED = new Integer(3);

	public static final List<Integer> STATUSES_LEGIT = Arrays
			.asList(new Integer[] { STATUS_SUCCEEDED, STATUS_NEW,
					STATUS_IN_TRANSACTION, STATUS_QUEUED });
}
