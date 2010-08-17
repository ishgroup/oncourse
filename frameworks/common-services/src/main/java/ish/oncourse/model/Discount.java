package ish.oncourse.model;

import ish.oncourse.math.Money;
import ish.oncourse.model.auto._Discount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;


public class Discount extends _Discount {

	private static final Logger LOG = Logger.getLogger(Discount.class);
	public static final Integer COMBINATION_ANY = Integer.valueOf(1);
	public static final Integer COMBINATION_NONE = Integer.valueOf(0);

	/**
	 * @param discounts
	 *            - a collection of discounts to filter.
	 * @param aClass
	 *            - the course class
	 * @return the discount value that could be obtained from the given list..
	 */
	public static BigDecimal discountValueForCourseClass(
			List<? extends Discount> discounts, CourseClass aClass) {
		BigDecimal result = Money.ZERO.toBigDecimal();
		Iterator<? extends Discount> discountsIterator = discountsForCourseClass(
				discounts, aClass).iterator();
		while (discountsIterator.hasNext()) {
			Discount d = discountsIterator.next();
			result = result.add(d.valueForCourseClass(aClass));
		}
		return result;
	}

	/**
	 * @param discounts
	 *            - a collection of discounts to filter.
	 * @param aClass
	 *            - the class.
	 * @return a collection of discounts that provide the best deal.
	 */
	public static <T extends Discount> List<T> discountsForCourseClass(
			List<T> discounts, CourseClass aClass) {
		return discountsForAbstractType(discounts, aClass);
	}

	@SuppressWarnings("unchecked")
	protected static <T extends Discount> List<T> discountsForAbstractType(
			List<T> discounts, CayenneDataObject obj) {
		if (!(obj instanceof CourseClass) && !(obj instanceof Enrolment)) {
			throw new IllegalStateException("Unknown abstract type");
		}

		List<T> localDiscounts = new ArrayList<T>();
		for (T discount : discounts) {
			localDiscounts.add((T) obj.getObjectContext().localObject(
					discount.getObjectId(), discount));
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("discounts : " + localDiscounts.size());
		}

		// calculate combined discount
		List<T> discountsToCombine = ExpressionFactory.matchExp(
				Discount.COMBINATION_TYPE_PROPERTY, COMBINATION_ANY).filterObjects(localDiscounts);
		LOG.debug("discountsToCombine.count " + discountsToCombine.size());
		BigDecimal resultCombined = Money.ZERO.toBigDecimal();
		Iterator<T> iteratorForCombined = discountsToCombine.iterator();
		while (iteratorForCombined.hasNext()) {
			Discount d = iteratorForCombined.next();
			if (obj instanceof Enrolment) {
				resultCombined = resultCombined.add(d.valueForEnrolment((Enrolment) obj));
			} else if (obj instanceof CourseClass) {
				resultCombined = resultCombined.add(d.valueForCourseClass((CourseClass) obj));
			}
		}
		List<T> discountsNotToCombine = ExpressionFactory.matchExp(
				Discount.COMBINATION_TYPE_PROPERTY, COMBINATION_NONE).filterObjects(localDiscounts);

		LOG.debug("discountsNotToCombine.count "
				+ discountsNotToCombine.size());
		BigDecimal resultNotCombined = Money.ZERO.toBigDecimal();
		T notCombinedDiscount = null;
		Iterator<T> iteratorForNotCombined = discountsNotToCombine.iterator();
		while (iteratorForNotCombined.hasNext()) {
			T d = iteratorForNotCombined.next();
			BigDecimal val = null;
			if (obj instanceof Enrolment) {
				val = d.valueForEnrolment((Enrolment) obj);
			} else if (obj instanceof CourseClass) {
				val = d.valueForCourseClass((CourseClass) obj);
			}

			if (val.compareTo(resultCombined) > 0) {
				if (resultNotCombined == null
						|| resultNotCombined.compareTo(val) < 0) {
					resultNotCombined = val;
					notCombinedDiscount = d;
				}
			}
		}
		LOG.debug("discount amount:" + resultNotCombined);

		int comparison = resultCombined.compareTo(resultNotCombined);
		if (comparison != 0) {
			if (comparison > 0) {
				return discountsToCombine;
			}
			return Arrays.asList(notCombinedDiscount);
		}
		return new ArrayList<T>();
	}

	/**
	 * @param enrolment
	 *            - an enrolment to determine the discount value.
	 * @return the discount if the student and class are applicable or zero.
	 */
	public BigDecimal valueForEnrolment(Enrolment enrolment) {
		boolean isEligible = false;
		if (isValidWithGracePeriod()) {
			isEligible = true;
			if (getStudentAge() != null && hasStudentAgeOperator()
					|| hasStudentPostcodes()
					|| getStudentEnrolledWithinDays() != null) {
				if (enrolment == null || enrolment.getStudent() == null) {
					isEligible = false;
				}
				if (isEligible && hasStudentAgeOperator()
						&& getStudentAge() != null) {
					if (enrolment.getStudent().getContact().getDateOfBirth() == null) {
						isEligible = false;
					} else {
						int enrolmentAge = enrolment.getStudent().getYearsOfAge();
						int discountAge = getStudentAge();
						int diff = enrolmentAge - discountAge;
						if (">".equals(getStudentAgeOperator()) && diff <= 0
								|| "<".equals(getStudentAgeOperator())
								&& diff >= 0
								|| "!=".equals(getStudentAgeOperator())
								&& diff == 0
								|| ">=".equals(getStudentAgeOperator())
								&& diff < 0
								|| "<=".equals(getStudentAgeOperator())
								&& diff > 0) {
							isEligible = false;
						}
					}
				}
			}
			if (isEligible && hasStudentPostcodes()) {
				List<String> postcodes = Arrays.asList(getStudentPostcodes().split("\\s*,\\s"));
				if (postcodes.indexOf(enrolment.getStudent().getContact().getPostcode()) == -1) {
					isEligible = false;
				}
			}
			if (isEligible && getStudentEnrolledWithinDays() != null) {
				int days = -1 * getStudentEnrolledWithinDays().intValue();
				Calendar timestamp = Calendar.getInstance();
				timestamp.set(0, 0, days, 0, 0, 0);
				Expression qualifier = ExpressionFactory.matchExp(
						Enrolment.STUDENT_PROPERTY, enrolment.getStudent()).andExp(
						ExpressionFactory.greaterOrEqualExp(
						Enrolment.CREATED_PROPERTY, timestamp.getTime())).andExp(
						ExpressionFactory.inExp(
						Enrolment.STATUS_PROPERTY,
						ISHPayment.STATUSES_LEGIT));
				// TODO get the count by some another way
				int count = enrolment.getObjectContext().performQuery(
						new SelectQuery(Enrolment.class, qualifier)).size();
				if (count == 0) {
					isEligible = false;
				}
			}
			if (isEligible && getDiscountConcessionTypes().size() > 0) {
				List<ConcessionType> dtypes = new ArrayList<ConcessionType>();
				for (DiscountConcessionType dct : getDiscountConcessionTypes()) {
					dtypes.add(dct.getConcessionType());
				}

				List<ConcessionType> stypes = new ArrayList<ConcessionType>();
				for (StudentConcession sc : enrolment.getStudent().getStudentConcessions()) {
					stypes.add(sc.getConcessionType());
				}

				if (LOG.isDebugEnabled()) {
					LOG.debug("dtypes:" + dtypes.size());
					LOG.debug("stypes:" + stypes.size());
				}

				boolean arrayContainsAnyObjectFromArray = false;
				for (ConcessionType type : dtypes) {
					if (stypes.contains(type)) {
						arrayContainsAnyObjectFromArray = true;
						break;
					}
				}
				if (!arrayContainsAnyObjectFromArray) {
					isEligible = false;
				}
			}
		}
		return isEligible ? valueForCourseClass(enrolment.getCourseClass())
				: Money.ZERO.toBigDecimal();
	}

	/**
	 * @param courseClass
	 *            the class to determine the discount for.
	 * @return zero if the class is not assigned for discount or a discount
	 *         value.
	 */
	public BigDecimal valueForCourseClass(CourseClass courseClass) {
		BigDecimal result = Money.ZERO.toBigDecimal();
		if (courseClass != null && courseClass.hasFeeIncTax()
				&& isValidWithGracePeriod()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("class - " + courseClass.getUniqueIdentifier()
						+ " discounting...");
				LOG.debug("class discounts:"
						+ courseClass.getDiscountCourseClasses());
			}
			List<Discount> discounts = new ArrayList<Discount>();
			for (DiscountCourseClass dcc : courseClass.getDiscountCourseClasses()) {
				discounts.add(dcc.getDiscount());
			}
			if (discounts.contains(this)) {
				if (getDiscountAmount() != null
						&& Money.ZERO.compareTo(getDiscountAmount()) < 0) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("applying discount $ : "
								+ getDiscountAmount());
					}
					result = result.add(getDiscountAmount());
				} else if (getDiscountRate() != null) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("applying discount % : " + getDiscountRate());
					}
					result = courseClass.getFeeExGst().multiply(
							getDiscountRate());

					if (getMaximumDiscount() != null
							&& Money.ZERO.compareTo(getMaximumDiscount()) < 0) {
						if (result.compareTo(getMaximumDiscount()) > 0) {
							result = getMaximumDiscount();
						}
					}

					if (getMinimumDiscount() != null
							&& Money.ZERO.compareTo(getMinimumDiscount()) < 0) {
						if (result.compareTo(getMinimumDiscount()) < 0) {
							result = getMinimumDiscount();
						}
					}
				}
				Money value = Money.valueOf(result);
				if (getRoundingMode() != null) {
					value = value.round(getRoundingMode());
				}

				result = value.toBigDecimal();
			}
		}
		return result;
	}

	public boolean isValidWithGracePeriod() {
		Date now = Calendar.getInstance().getTime();
		if (getValidFrom() != null) {
			int compare = getValidFrom().compareTo(now);
			if (compare == 1) {
				return false;
			}
		}
		if (getValidTo() != null) {
			Calendar c = Calendar.getInstance();
			c.set(0, 0, 0, 4, 0, 0);
			Date nowWithGrace = c.getTime();
			int compare = getValidTo().compareTo(nowWithGrace);
			if (compare == -1) {
				return false;
			}
		}
		return true;
	}

	public boolean hasStudentAgeOperator() {
		// String
		return getStudentAgeOperator() != null
				&& !getStudentAgeOperator().matches("\\s+");
	}

	public boolean hasStudentPostcodes() {
		// String
		return getStudentPostcodes() != null
				&& !getStudentPostcodes().matches("\\s+");
	}
}
