package ish.oncourse.model;

import ish.oncourse.model.auto._Discount;

import java.util.Date;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.log4j.Logger;

public class Discount extends _Discount implements Queueable {

	private static final Logger LOG = Logger.getLogger(Discount.class);
	public static final String PROMOTIONS_KEY = "promotions";

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId().getIdSnapshot().get(
				ID_PK_COLUMN) : null;
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
		return getCode() != null && !getCode().equals("");
	}

	public boolean hasEligibilityFilter() {
		return getStudentEnrolledWithinDays() != null || getStudentAge() != null
				|| !(getDiscountConcessionTypes() == null || getDiscountConcessionTypes().isEmpty())
				|| getStudentPostcodes() != null;
	}

	public String getEligibilityConditions() {
		StringBuffer result = new StringBuffer();
		if (getStudentEnrolledWithinDays() != null) {
			result.append("enrolled within ").append(getStudentEnrolledWithinDays());
		}
		if (getStudentAge() != null) {
			if (result.length() != 0) {
				result.append(",");
			}
			result.append(" of age ").append(getStudentAgeOperator()).append(getStudentAge());
		}
		if (getDiscountConcessionTypes() != null && !getDiscountConcessionTypes().isEmpty()) {
			if (result.length() != 0) {
				result.append(",");
			}
			result.append(" with concessions:");
			for (DiscountConcessionType type : getDiscountConcessionTypes()) {
				result.append(" ").append(type.getConcessionType().getName());
			}
		}
		if (getStudentPostcodes() != null) {
			if (result.length() != 0) {
				result.append(",");
			}
			result.append(" with poscodes: ").append(getStudentPostcodes());
		}
		return result.toString();
	}
}
