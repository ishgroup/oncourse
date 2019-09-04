package ish.oncourse.model;

import ish.math.Money;
import ish.math.MoneyRounding;
import ish.oncourse.cayenne.DiscountInterface;
import ish.oncourse.model.auto._Discount;
import ish.oncourse.utils.QueueableObjectUtils;
import ish.util.DateTimeUtil;
import org.apache.cayenne.exp.Expression;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.Date;

public class Discount extends _Discount implements DiscountInterface, Queueable {
	private static final long serialVersionUID = 2398560052340467949L;
	private static final Logger logger = LogManager.getLogger();
	public static final String PROMOTIONS_KEY = "promotions";

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	protected void onPrePersist() {
		if (getLimitPreviousEnrolment() == null) {
			setLimitPreviousEnrolment(false);
		}
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

	public static Expression getCurrentDateFilterForDiscountCourseClass(Date classStartDate) {
		Date now = new Date();

		Expression validToExp = DiscountCourseClass.DISCOUNT.dot(Discount.VALID_TO).isNull().andExp(DiscountCourseClass.DISCOUNT.dot(Discount.VALID_TO_OFFSET).isNull());
		validToExp = validToExp.orExp(DiscountCourseClass.DISCOUNT.dot(Discount.VALID_TO).isNotNull().andExp(DiscountCourseClass.DISCOUNT.dot(Discount.VALID_TO).gt(now)));

		Expression validFromExp = DiscountCourseClass.DISCOUNT.dot(Discount.VALID_FROM).isNull().andExp(DiscountCourseClass.DISCOUNT.dot(Discount.VALID_FROM_OFFSET).isNull());
		validFromExp = validFromExp.orExp(DiscountCourseClass.DISCOUNT.dot(Discount.VALID_FROM).isNotNull().andExp(DiscountCourseClass.DISCOUNT.dot(Discount.VALID_FROM).lt(now)));

		// apply discounts with offsets (valid from offset, valid to offset) only when courseClass has start date time.
		if (classStartDate != null) {
			int startClassOffsetInDays = DateTimeUtil.getDaysLeapYearDaylightSafe(classStartDate, now);

			validToExp = validToExp.orExp(DiscountCourseClass.DISCOUNT.dot(Discount.VALID_TO_OFFSET).isNotNull().andExp(DiscountCourseClass.DISCOUNT.dot(Discount.VALID_TO_OFFSET).gte(startClassOffsetInDays)));
			validFromExp = validFromExp.orExp(DiscountCourseClass.DISCOUNT.dot(Discount.VALID_FROM_OFFSET).isNotNull().andExp(DiscountCourseClass.DISCOUNT.dot(Discount.VALID_FROM_OFFSET).lte(startClassOffsetInDays)));
		}

		return validToExp.andExp(validFromExp);
	}
	
	public boolean isPromotion() {
		return getCode() != null && !StringUtils.EMPTY.equals(getCode());
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}

	@Override
	public BigDecimal getDiscountPercent() {
		return super.getDiscountRate();
	}

	@Override
	public Money getDiscountDollar() {
		return super.getDiscountAmount();
	}

	@Override
	public Money getDiscountMin() {
		return super.getMinimumDiscount();
	}

	@Override
	public Money getDiscountMax() {
		return super.getMaximumDiscount();
	}

	@Override
	public MoneyRounding getRounding() {
		return super.getRoundingMode();
	}
}
