package ish.oncourse.model;

import ish.oncourse.model.auto._Discount;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.exp.Expression;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class Discount extends _Discount implements Queueable {
	private static final long serialVersionUID = 2398560052340467949L;
	private static final Logger logger = LogManager.getLogger();
	public static final String PROMOTIONS_KEY = "promotions";

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	/**
	 * Returns filter for retrieving the current discounts(with valid or
	 * undefined date range)
	 * 
	 * @return expression
	 */
	public static Expression getCurrentDateFilter() {
		Date now = new Date();

		Expression e = Discount.VALID_TO.gt(now).orExp(Discount.VALID_TO.isNull());
		e = e.andExp(Discount.VALID_FROM.lt(now).orExp(Discount.VALID_FROM.isNull()));
		return e;
	}

	public boolean isPromotion() {
		return getCode() != null && !StringUtils.EMPTY.equals(getCode());
	}

	public boolean hasEligibilityFilter() {
		return getStudentEnrolledWithinDays() != null || getStudentAge() != null
				|| !(getDiscountConcessionTypes() == null || getDiscountConcessionTypes().isEmpty())
				|| getStudentPostcodes() != null;
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
