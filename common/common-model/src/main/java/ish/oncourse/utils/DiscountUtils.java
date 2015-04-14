package ish.oncourse.utils;

import ish.common.types.DiscountType;
import ish.math.Money;
import ish.oncourse.model.*;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * Util class for calculating discounts applying results.
 * 
 * @author ksenia
 * 
 */
public class DiscountUtils {
	private static final String SPACE_CHARACTER = " ";
	private static final String WITH_POSCODES_CONDITION_TEXT = " with poscodes: ";
	private static final String WITH_MEMBERSHIPS_CONDITION_TEXT = " with memberships:";
	private static final String WITH_CONCESSIONS_CONDITION_TEXT = " with concessions:";
	private static final String OF_AGE_CONDITION_TEXT = " of age ";
	private static final String COMMA_CHARACTER = ",";
	private static final String ENROLLED_WITHIN_CONDITION_TEXT = "enrolled within ";
	private static final String CONDITION_SEPARATOR_STRING = "; ";
	private static final String FOR_STUDENTS_START_STRING = "for students: ";

	/**
	 * Returns the discount value for the given price if apply the given
	 * discount.
	 * 
	 * @param discount
	 *            - the given discount.
	 * @param price
	 *            - the price for discount
	 * @return the discount value
	 */
	public static Money discountValue(Discount discount, Money price) {
		if (price.isZero()) {
			return price;
		}
		Money discountValue = Money.ZERO;
		BigDecimal discountRate = discount.getDiscountRate();
		DiscountType discountType = discount.getDiscountType();

		if (discountType == null) {
			if (discountRate == null) {
				discountValue = discount.getDiscountAmount();
			} else {
				discountValue = price.multiply(discountRate);
			}
		} else {
			switch (discountType) {
			case FEE_OVERRIDE:
				discountValue = price.subtract(discount.getDiscountAmount());
				break;
			case DOLLAR:
				discountValue = discount.getDiscountAmount();
				break;
			case PERCENT:
				discountValue = price.multiply(discountRate);
				discountValue = discountValue.round(discount.getRoundingMode());
				break;
			}
		}

		Money maximumDiscount = discount.getMaximumDiscount();
		if (Money.ZERO.isLessThan(maximumDiscount) && discountValue.compareTo(maximumDiscount) > 0) {
			discountValue = maximumDiscount;
		} else {
			Money minimumDiscount = discount.getMinimumDiscount();
			if (Money.ZERO.isLessThan(minimumDiscount) && discountValue.compareTo(minimumDiscount) < 0) {
				discountValue = minimumDiscount;
			}
		}
		if (price.compareTo(discountValue) < 0) {
			return price;
		}
		return discountValue;
	}

	/**
	 * Returns the discount value for the given price if apply the discounts
	 * from the given list(doesn't check the applicability, just calculate).
	 * 
	 * @param discounts
	 *            - a collection of discounts to apply.
	 * @param price
	 *            - the price for discount
	 * @return the discount value
	 */
	public static Money discountValueForList(List<Discount> discounts, Money price) {
		Money result = Money.ZERO;
		if (discounts != null) {
			for (Discount d : discounts) {
				result = result.add(discountValue(d, price));
			}
		}
		if (price.compareTo(result) < 0) {
			return price;
		}
		return result;
	}

	/**
	 * Chooses the best option (the max discount value) from the proposed
	 * discounts : uses the combination of "combinable" discounts, or uses the
	 * best one of "uncombinable".<br/>
	 * The processing of combined/notToCombine discounts is based on
	 * angel/client/ish.oncourse.cayenne.InvoiceLine.updateDiscount().
	 * 
	 * @param discounts
	 * @param feeExGst
	 * @return
	 */

	public static List<Discount> chooseBestDiscountsVariant(List<Discount> discounts, Money feeExGst) {
		Vector<Discount> chosenDiscounts = new Vector<>();
		if (discounts != null && !discounts.isEmpty()) {
			// figure out the best deal for the customer.
			// first try all the discounts which could be combined.
			Expression exp = ExpressionFactory.matchExp(Discount.COMBINATION_TYPE_PROPERTY, Boolean.TRUE);
			List<Discount> discountsToCombine = (List<Discount>) exp.filterObjects(discounts);
			List<Discount> discountsNotToCombine = (List<Discount>) exp.notExp().filterObjects(discounts);

			Money maxDiscount = Money.ZERO;
			Discount bestDeal = null;
			maxDiscount = DiscountUtils.discountValueForList(discountsToCombine, feeExGst);

			for (Discount d : discountsNotToCombine) {
				Money val = DiscountUtils.discountValue(d, feeExGst);

				if (val.compareTo(maxDiscount) > 0) {
					bestDeal = d;
					maxDiscount = val;
				}
			}

			if (bestDeal == null) {
				// go with combined discounts, remove all not combinable
				chosenDiscounts.addAll(discountsToCombine);
			} else {
				// go with not combined discount
				chosenDiscounts.add(bestDeal);
			}
		}
		return chosenDiscounts;

	}

	/**
	 * Retrieves the earliest expiry date of a bunch of discounts to apply.
	 * Useful when a bunch of combinable discounts applied to a price. when this
	 * is just a one discount send, just returns its expiry date. If no expiry
	 * date is defined, returns null;
	 * 
	 * @param discounts
	 * @return
	 */
	public static Date earliestExpiryDate(List<Discount> discounts) {
		Date result = null;
		for (Discount discount : discounts) {

			Date expiry = discount.getValidTo();
			if (expiry != null) {
				Calendar expiryDate = Calendar.getInstance();
				expiryDate.setTime(expiry);
				// the whole last day discount should be available
				expiryDate.set(Calendar.HOUR_OF_DAY, 23);
				expiryDate.set(Calendar.MINUTE, 59);
				expiryDate.set(Calendar.SECOND, 59);
				if (result == null) {
					result = expiryDate.getTime();
				} else {
					if (expiryDate.getTime().before(result)) {
						result = expiry;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Retrieves the eligibility conditions of a bunch of discounts to apply.
	 * Useful when a bunch of combinable discounts applied to a price. when this
	 * is just a one discount send, just returns its eligibility condition. If
	 * no contact filter is defined, returns null;
	 * 
	 * @param discounts
	 * @return
	 */
	public static String getEligibilityConditions(List<Discount> applicableDiscounts) {
		StringBuilder result = new StringBuilder(FOR_STUDENTS_START_STRING);
		boolean hasFilter = false;
		for (Discount discount : applicableDiscounts) {
			if ((discount.hasEligibilityFilter() 
				|| (discount.getDiscountMembershipProducts() != null && !discount.getDiscountMembershipProducts().isEmpty())) 
				&& result.indexOf(buildEligibilityConditionText(discount)) == -1) {
				if (hasFilter) {
					result.append(CONDITION_SEPARATOR_STRING);
				}
				hasFilter = true;
				result.append(buildEligibilityConditionText(discount));
			}
		}
		if (hasFilter) {
			return result.toString();
		}
		return null;
	}
	
	public static boolean hasAnyFiltering(Discount discount) {
		return discount.getStudentEnrolledWithinDays() != null || discount.getStudentAge() != null || 
			(discount.getDiscountConcessionTypes() != null && !discount.getDiscountConcessionTypes().isEmpty()) || discount.getStudentPostcodes() != null
			|| (discount.getDiscountMembershipProducts() != null && !discount.getDiscountMembershipProducts().isEmpty());
	}
	
	public static List<Discount> getFilteredDiscounts(CourseClass courseClass) {
		List<Discount> classDiscountsWithoutCode = (ExpressionFactory.matchExp(Discount.CODE_PROPERTY, null))
			.orExp(ExpressionFactory.matchExp(Discount.CODE_PROPERTY, StringUtils.EMPTY)).filterObjects(getPotentialClassDiscounts(courseClass));
		List<Discount> discounts = new ArrayList<>(classDiscountsWithoutCode.size());
		for (Discount discount : classDiscountsWithoutCode) {
			if (hasAnyFiltering(discount)) {
				discounts.add(discount);
			}
		}
		return discounts;
	}
	
	@SuppressWarnings("unchecked")
	private static List<Discount> getPotentialClassDiscounts(CourseClass courseClass) {
		SelectQuery query = new SelectQuery(Discount.class, ExpressionFactory.matchExp(
			Discount.DISCOUNT_COURSE_CLASSES_PROPERTY + "." + DiscountCourseClass.COURSE_CLASS_PROPERTY, courseClass)
				.andExp(Discount.getCurrentDateFilter()));
		return courseClass.getObjectContext().performQuery(query);
	}
	
	private static String buildEligibilityConditionText(final Discount discount) {
		StringBuilder result = new StringBuilder();
		if (discount.getStudentEnrolledWithinDays() != null) {
			result.append(ENROLLED_WITHIN_CONDITION_TEXT).append(discount.getStudentEnrolledWithinDays());
		}
		if (discount.getStudentAge() != null) {
			if (result.length() != 0) {
				result.append(COMMA_CHARACTER);
			}
			result.append(OF_AGE_CONDITION_TEXT).append(discount.getStudentAgeOperator()).append(discount.getStudentAge());
		}
		if (discount.getDiscountConcessionTypes() != null && !discount.getDiscountConcessionTypes().isEmpty()) {
			if (result.length() != 0) {
				result.append(COMMA_CHARACTER);
			}
			result.append(WITH_CONCESSIONS_CONDITION_TEXT);
			for (DiscountConcessionType type : discount.getDiscountConcessionTypes()) {
				result.append(SPACE_CHARACTER).append(type.getConcessionType().getName())
					.append(discount.getDiscountConcessionTypes().indexOf(type) < (discount.getDiscountConcessionTypes().size() -1) 
						? COMMA_CHARACTER : StringUtils.EMPTY);
			}
		}
		if (discount.getDiscountMembershipProducts() != null && !discount.getDiscountMembershipProducts().isEmpty()) {
			if (result.length() != 0) {
				result.append(COMMA_CHARACTER);
			}
			result.append(WITH_MEMBERSHIPS_CONDITION_TEXT);
			for (DiscountMembership membership : discount.getDiscountMembershipProducts()) {
				result.append(SPACE_CHARACTER).append(membership.getMembershipProduct().getName())
					.append(discount.getDiscountMembershipProducts().indexOf(membership) < (discount.getDiscountMembershipProducts().size() -1)
						? COMMA_CHARACTER : StringUtils.EMPTY);
			}
		}
		if (discount.getStudentPostcodes() != null) {
			if (result.length() != 0) {
				result.append(COMMA_CHARACTER);
			}
			result.append(WITH_POSCODES_CONDITION_TEXT).append(discount.getStudentPostcodes());
		}
		return result.toString();
	}
}
