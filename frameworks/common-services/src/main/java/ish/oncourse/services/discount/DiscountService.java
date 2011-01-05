package ish.oncourse.services.discount;

import ish.math.Money;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountConcessionType;
import ish.oncourse.model.DiscountCourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.cookies.ICookiesService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

public class DiscountService implements IDiscountService {

	@Inject
	private ICookiesService cookiesService;

	@Inject
	private ICayenneService cayenneService;

	public static final String AGE_UNDER = "<";
	public static final String AGE_OVER = ">";

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#getApplicableDiscounts(ish.oncourse.model.CourseClass)
	 * 
	 *      The implementation is brought from
	 *      angel/client/ish.oncourse.cayenne.
	 *      Discount.getApplicableDiscounts(CourseClass courseClass)
	 */
	public List<Discount> getApplicableDiscounts(CourseClass courseClass) {
		List<Discount> results = new ArrayList<Discount>();

		results.addAll(courseClass.getDiscounts());

		Expression e = ExpressionFactory.matchExp(Discount.CODE_PROPERTY, null);
		e = e.andExp(getCurrentDateFilter());

		results = e.filterObjects(results);

		return results;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#filterDiscounts(java.util.List,
	 *      ish.oncourse.model.CourseClass)
	 */
	public List<Discount> filterDiscounts(List<Discount> discounts, CourseClass courseClass) {
		if (discounts == null) {
			return Collections.emptyList();
		}
		Expression e = ExpressionFactory.matchExp(Discount.DISCOUNT_COURSE_CLASSES_PROPERTY + "."
				+ DiscountCourseClass.COURSE_CLASS_PROPERTY, courseClass);
		e = e.andExp(getCurrentDateFilter());

		return e.filterObjects(discounts);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#getEnrolmentDiscounts(ish.oncourse.model.Enrolment)
	 */
	public List<Discount> getEnrolmentDiscounts(Enrolment enrolment) {
		List<Discount> result = new ArrayList<Discount>();

		List<Discount> potentialDiscounts = getApplicableDiscounts(enrolment.getCourseClass());
		potentialDiscounts.addAll(filterDiscounts(getPromotions(), enrolment.getCourseClass()));

		for (Discount discount : potentialDiscounts) {
			if (isStudentEligibile(enrolment.getStudent(), discount)) {
				result.add(discount);
			}
		}

		result = chooseBestDiscountsVariant(result, enrolment.getCourseClass());

		return result;
	}

	/**
	 * Determines if the given student is eligible for this Discount, based on:
	 * enrolled within X days; student age; postcode; and concessions (test not
	 * performed if this Discount is not related to any ConcessionTypes). If the
	 * student has ANY of the concessions related to this Discount, they will be
	 * eligible. If the student has none of them (and one or more are related to
	 * this discount), they will NOT be eligible.
	 * 
	 * the implementation of this method is brought from
	 * angel/client/ish.oncourse.cayenne.Discount#isStudentEligibile(Student
	 * student)[97]
	 * 
	 * @param student
	 * @return
	 */
	public boolean isStudentEligibile(Student student, Discount discount) {
		if (student == null || discount == null) {
			return false;
		}

		if (discount.getStudentEnrolledWithinDays() != null) {
			if (student.getEnrolments() == null || student.getEnrolments().size() == 0) {
				return false;// not eligibile
			}

			boolean notEligibile = true;
			Calendar tresholdDate = new GregorianCalendar();
			tresholdDate.add(Calendar.DATE, 0 - discount.getStudentEnrolledWithinDays());

			for (Enrolment enr : student.getEnrolments()) {
				Date startDate = enr.getCourseClass().getStartDate();
				if (startDate.after(tresholdDate.getTime())) {
					notEligibile = false;// not eligibile
				}
			}

			if (notEligibile) {
				return false;// not eligibile
			}
		}
		if (discount.getStudentAge() != null) {
			if (student.getContact().getDateOfBirth() == null) {
				return false;// not eligibile
			}

			Calendar studentBirthDay = new GregorianCalendar();
			studentBirthDay.setTime(student.getContact().getDateOfBirth());

			Calendar thresholdYear = new GregorianCalendar();

			int age = discount.getStudentAge();

			thresholdYear.add(Calendar.YEAR, 0 - age);

			if (AGE_UNDER.equals(discount.getStudentAgeOperator())
					&& studentBirthDay.before(thresholdYear)) {
				return false;// not eligibile
			} else if (AGE_OVER.equals(discount.getStudentAgeOperator())
					&& studentBirthDay.after(thresholdYear)) {
				return false;// not eligibile
			} else if (!AGE_OVER.equals(discount.getStudentAgeOperator())
					&& !AGE_UNDER.equals(discount.getStudentAgeOperator())) {
				return false;// not eligibile
			}
		}
		if (!discount.getDiscountConcessionTypes().isEmpty()) {
			boolean notEligibile = true;
			for (DiscountConcessionType dct : discount.getDiscountConcessionTypes()) {
				for (StudentConcession concession : student.getStudentConcessions()) {
					if (concession.getConcessionType().equals(dct.getConcessionType())) {
						if (!Boolean.TRUE.equals(concession.getConcessionType().getHasExpiryDate())
								|| (concession.getExpiresOn() != null && concession.getExpiresOn()
										.after(new Date()))) {
							notEligibile = false;
							break;
						}
					}
				}
			}
			if (notEligibile)
				return false; // does not have any of the concession types that
								// give this discount
		}
		if (discount.getStudentPostcodes() != null) {
			List<String> postcodes = Arrays
					.asList(discount.getStudentPostcodes().split("\\s*,\\s"));
			if (!postcodes.isEmpty()) {
				if (student.getContact().getPostcode() == null) {
					return false;// not eligibile
				}
				if (!postcodes.contains(student.getContact().getPostcode())) {
					return false;// not eligibile
				}
			}
		}
		return true;// eligibile
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#chooseBestDiscountsVariant(java.util.List,
	 *      ish.oncourse.model.CourseClass) The processing of
	 *      combined/notToCombine discounts is based on
	 *      angel/client/ish.oncourse.cayenne.InvoiceLine.updateDiscount()
	 */
	public List<Discount> chooseBestDiscountsVariant(List<Discount> discounts, CourseClass aClass) {
		Vector<Discount> chosenDiscounts = new Vector<Discount>();
		if (discounts != null && !discounts.isEmpty()) {
			// figure out the best deal for the customer.
			// first try all the discounts which could be combined.
			Expression exp = ExpressionFactory.matchExp(Discount.COMBINATION_TYPE_PROPERTY,
					Boolean.TRUE);
			List<Discount> discountsToCombine = (List<Discount>) exp.filterObjects(discounts);
			List<Discount> discountsNotToCombine = (List<Discount>) exp.notExp().filterObjects(
					discounts);

			Money maxDiscount = Money.ZERO;
			Discount bestDeal = null;
			Money feeExGst = aClass.getFeeExGst();
			maxDiscount = discountValueForList(discountsToCombine, feeExGst);

			for (Discount d : discountsNotToCombine) {
				Money val = discountValue(d, feeExGst);

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
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#discountValueForList(java.util.List,
	 *      ish.math.Money)
	 */
	public Money discountValueForList(List<Discount> discounts, Money price) {
		Money result = Money.ZERO;
		for (Discount d : discounts) {
			result = result.add(discountValue(d, price));
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#discountedValueForList(java.util.List,
	 *      ish.math.Money)
	 */
	public Money discountedValueForList(List<Discount> discounts, Money price) {
		return price.subtract(discountValueForList(discounts, price));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#getConcessionDiscounts(ish.oncourse.model.CourseClass)
	 */
	public List<Discount> getConcessionDiscounts(CourseClass aClass) {
		List<Discount> availableDiscounts = getApplicableDiscounts(aClass);

		List<Discount> discounts = new ArrayList<Discount>(availableDiscounts.size());
		for (Discount discount : availableDiscounts) {
			if (!discount.getDiscountConcessionTypes().isEmpty()) {
				discounts.add(discount);
			}
		}
		return discounts;
	}

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
	public Money discountValue(Discount discount, Money price) {
		Money discountValue = Money.ZERO;
		BigDecimal discountRate = discount.getDiscountRate();
		if (discountRate == null) {
			discountRate = discount.getDiscountAmount().divide(price).toBigDecimal();
		}
		discountValue = price.multiply(discountRate);
		Money maximumDiscount = discount.getMaximumDiscount();
		if (Money.ZERO.isLessThan(maximumDiscount) && discountValue.compareTo(maximumDiscount) > 0) {
			discountValue = maximumDiscount;
		} else {
			Money minimumDiscount = discount.getMinimumDiscount();
			if (Money.ZERO.isLessThan(minimumDiscount)
					&& discountValue.compareTo(minimumDiscount) < 0) {
				discountValue = minimumDiscount;
			}
		}
		return discountValue;
	}

	/**
	 * Returns the discounted value for the given price if apply the given
	 * discount.
	 * 
	 * @param discount
	 *            - the given discount.
	 * @param price
	 *            - the price for discount
	 * @return the discounted value
	 */
	public Money discountedValue(Discount discount, Money price) {
		return price.subtract(discountValue(discount, price));
	}

	/**
	 * Returns filter for retrieving the current discounts(with valid or
	 * undefined date range)
	 * 
	 * @return expression
	 */
	private Expression getCurrentDateFilter() {
		Date now = new Date();

		Expression e = ExpressionFactory.greaterExp(Discount.VALID_TO_PROPERTY, now).orExp(
				ExpressionFactory.matchExp(Discount.VALID_TO_PROPERTY, null));
		e = e.andExp(ExpressionFactory.lessExp(Discount.VALID_FROM_PROPERTY, now).orExp(
				ExpressionFactory.matchExp(Discount.VALID_FROM_PROPERTY, null)));
		return e;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#getPromotions()
	 */
	public List<Discount> getPromotions() {
		String[] discountIds = cookiesService.getCookieCollectionValue("promotions");
		return loadByIds(discountIds);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.discount.IDiscountService#loadByIds(java.lang.Object[])
	 */
	@Override
	public List<Discount> loadByIds(Object... ids) {
		if (ids == null || ids.length == 0) {
			return Collections.emptyList();
		}

		SelectQuery q = new SelectQuery(Discount.class);
		q.andQualifier(ExpressionFactory.inDbExp(Discount.ID_PK_COLUMN, ids));

		return cayenneService.sharedContext().performQuery(q);
	}

}
