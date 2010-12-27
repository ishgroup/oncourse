package ish.oncourse.services.discount;

import ish.math.Money;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.Enrolment;

import java.util.List;

public interface IDiscountService {
	/**
	 * Returns the list of discounts, applicable to the given class: current
	 * discounts of this class without promo codes.
	 * 
	 * @param courseClass
	 *            given class
	 * @return list of discounts
	 */
	List<Discount> getApplicableDiscounts(CourseClass courseClass);

	/**
	 * Filters the given list of discounts and returns the discounts, applicable
	 * to the given class.
	 * 
	 * @param courseClass
	 *            given class
	 * @return list of discounts
	 */
	List<Discount> filterDiscounts(List<Discount> discounts, CourseClass courseClass);

	/**
	 * Returns the best discounting variant for this enrolment assuming the
	 * applicability to enrolment's courseClass and student's eligibility for
	 * discount.
	 * 
	 * @param enrolment
	 *            the enrolment under consideration
	 * @return list of discounts
	 */
	List<Discount> getEnrolmentDiscounts(Enrolment enrolment);

	/**
	 * Chooses the best option (the max discount value) from the proposed
	 * discounts (assuming all the discounts in list are applicable): uses the
	 * combination of "combinable" discounts, or uses the best one of
	 * "uncombinable"
	 * 
	 * @param discounts
	 *            proposed discounts(eg promotions)
	 * @param aClass
	 *            the given class
	 * @return
	 */
	List<Discount> chooseBestDiscountsVariant(List<Discount> discounts, CourseClass aClass);

	/**
	 * All discounts bound to the given courseClass that require concessions
	 * instead of discount codes.
	 * 
	 * @param aClass
	 *            - the course class
	 * 
	 * @return the list of discounts.
	 */
	List<Discount> getConcessionDiscounts(CourseClass aClass);

	/**
	 * Returns the discount value for the given price if apply the discounts
	 * from the given list(don't check the applicability, just calculate).
	 * 
	 * @param discounts
	 *            - a collection of discounts to apply.
	 * @param price
	 *            - the price for discount
	 * @return the discount value
	 */
	Money discountValueForList(List<Discount> discounts, Money price);

	/**
	 * Returns the discounted value for the given price if apply the discounts
	 * from the given list(don't check the applicability, just calculate).
	 * 
	 * @param discounts
	 *            - a collection of discounts to apply.
	 * @param price
	 *            - the price for discount
	 * @return the discounted value
	 */
	Money discountedValueForList(List<Discount> discounts, Money price);

	/**
	 * Retrieves the list of promotions(discounts with "promo-codes") stored in cookies
	 * @return
	 */
	List<Discount> getPromotions();
	
	/**
	 * Retrieves the discounts with the given ids.
	 * 
	 * @param ids the array of ids for search.
	 * @return list of discounts.
	 */
	List<Discount> loadByIds(Object... ids);
}
