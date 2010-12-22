package ish.oncourse.services.discount;

import ish.math.Money;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;

import java.math.BigDecimal;
import java.util.List;

public interface IDiscountService {
	/**
	 * Returns the list of discounts, applicable to the given class:
	 * current discounts of this class without promo codes.
	 * 
	 * @param courseClass given class
	 * @return list of discounts
	 */
	List<Discount> getApplicableDiscounts(CourseClass courseClass);
	
	/**
	 * Chooses the best option (the max discount value) from the proposed discounts: 
	 * use the combination of "combinable" discounts, or use the best of "uncombinable"
	 * @param discounts proposed discounts(eg promotions)
	 * @param aClass the given class
	 * @return
	 */
	List<Discount> chooseDiscounts(List<Discount> discounts, CourseClass aClass);
	
	/**
	 * Returns the discounted fee (without tax) for the given CourseClass 
	 * if apply the discounts that could be obtained from the given list.
	 * @param discounts
	 *            - a collection of discounts to filter.
	 * @param aClass
	 *            - the course class
	 * @return the discounted value
	 */
	Money discountedFeeExTax(List<Discount> discounts, CourseClass aClass);
	
	/**
	 * Returns the discounted fee (with tax) for the given CourseClass 
	 * if apply the discounts that could be obtained from the given list.
	 * @param discounts
	 *            - a collection of discounts to filter.
	 * @param aClass
	 *            - the course class
	 * @return the discounted value
	 */
	Money discountedFeeIncTax(List<Discount> discounts, CourseClass aClass);
	
	/**
	 * Returns the discount value for the given CourseClass
	 * if apply the discounts that could be obtained from the given list.
	 * @param discounts
	 *            - a collection of discounts to filter.
	 * @param aClass
	 *            - the course class
	 * @return the discount value
	 */
	Money discountValueForListFiltered(List<Discount> discounts,  CourseClass aClass);
	
	/**
	 * Returns the discount value for the given price 
	 * if apply the discounts from the given list.
	 * @param discounts
	 *            - a collection of discounts to apply.
	 * @param price
	 *            - the price for discount
	 * @return the discount value
	 */
	Money discountValueForList(List<Discount> discounts, Money price);
	
	/**
	 * Returns the discount value for the given price 
	 * if apply the given discount.
	 * @param discount
	 *            - the given discount.
	 * @param price
	 *            - the price for discount
	 * @return the discount value
	 */
	Money discountValue(Discount discount, Money price);


}
