package ish.oncourse.services.discount

import groovy.time.TimeCategory
import ish.math.Money
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Discount
import ish.oncourse.model.DiscountCourseClass

class WebDiscountUtils {
	
	def static List<DiscountItem> sortByDiscountValue(List<DiscountCourseClass> discounts, Money classFee, BigDecimal tax) {

		def discountItems = new ArrayList<>();
		
		if (!discounts.empty) {
			discounts.groupBy { DiscountCourseClass d -> ish.util.DiscountUtils.getDiscountedFee(d, classFee, tax) }
					.sort { it.key }
					.each { Money fee, List<DiscountCourseClass> classDiscounts ->
				discountItems << new DiscountItem(discounts: classDiscounts*.discount, feeIncTax: fee).init()
			}
		}
		return discountItems;
	}


	/**
	 * Retrieves expiry date of discount to apply.
	 *
	 * @param discount
	 * @return
	 */
	def static Date expiryDate(Discount discount, Date classStart) {
		Date result = null

		if (discount.validTo) {
			Calendar expiryDate = Calendar.getInstance();
			expiryDate.setTime(discount.validTo);
			// the whole last day discount should be available
			expiryDate.set(Calendar.HOUR_OF_DAY, 23);
			expiryDate.set(Calendar.MINUTE, 59);
			expiryDate.set(Calendar.SECOND, 59);
			result = expiryDate.getTime();

		} else if (discount.validToOffset && classStart) {
			Calendar expiryDate = Calendar.getInstance();
			expiryDate.setTime(classStart);
			// the whole last day discount should be available
			expiryDate.set(Calendar.HOUR_OF_DAY, 23);
			expiryDate.set(Calendar.MINUTE, 59);
			expiryDate.set(Calendar.SECOND, 59);
			expiryDate.add(Calendar.DATE, discount.validToOffset);
			result = expiryDate.getTime();
		}

		return result;
	}
}
