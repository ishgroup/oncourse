package ish.oncourse.model;

import ish.oncourse.model.auto._Discount;

import java.util.Date;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.log4j.Logger;


public class Discount extends _Discount {

	private static final Logger LOG = Logger.getLogger(Discount.class);
	public static final String PROMOTIONS_KEY="promotions";

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
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

}
