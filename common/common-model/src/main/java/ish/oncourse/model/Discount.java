package ish.oncourse.model;

import ish.oncourse.model.auto._Discount;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Date;

public class Discount extends _Discount implements Queueable {
	private static final long serialVersionUID = 2398560052340467949L;
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(Discount.class);
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

		Expression e = ExpressionFactory.greaterExp(Discount.VALID_TO_PROPERTY, now).orExp(
				ExpressionFactory.matchExp(Discount.VALID_TO_PROPERTY, null));
		e = e.andExp(ExpressionFactory.lessExp(Discount.VALID_FROM_PROPERTY, now).orExp(
				ExpressionFactory.matchExp(Discount.VALID_FROM_PROPERTY, null)));
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
