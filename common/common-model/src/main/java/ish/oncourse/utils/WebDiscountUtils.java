package ish.oncourse.utils;

import ish.oncourse.model.*;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang.StringUtils;
import java.util.*;

/**
 * Util class for calculating discounts applying results.
 * 
 * @author ksenia
 * 
 */
public class WebDiscountUtils {

	/**
	 * Retrieves expiry date of discount to apply.
	 * 
	 * @param discount
	 * @return
	 */
	public static Date expiryDate(Discount discount, CourseClass courseClass) {
		Date result = null;

		Date expiry = discount.getValidTo();
		Integer expiryOffset = discount.getValidToOffset();
		if (expiry != null) {
			Calendar expiryDate = Calendar.getInstance();
			expiryDate.setTime(expiry);
			// the whole last day discount should be available
			expiryDate.set(Calendar.HOUR_OF_DAY, 23);
			expiryDate.set(Calendar.MINUTE, 59);
			expiryDate.set(Calendar.SECOND, 59);
			result = expiryDate.getTime();
			
		} else if (expiryOffset != null && courseClass.getStartDate() != null) {
			Calendar expiryDate = Calendar.getInstance();
			expiryDate.setTime(courseClass.getStartDate());
			// the whole last day discount should be available
			expiryDate.set(Calendar.HOUR_OF_DAY, 23);
			expiryDate.set(Calendar.MINUTE, 59);
			expiryDate.set(Calendar.SECOND, 59);
			expiryDate.add(Calendar.DATE, expiryOffset);
			result = expiryDate.getTime();
		}

		return result;
	}
	
	public static boolean hasAnyFiltering(Discount discount) {
		return discount.getStudentEnrolledWithinDays() != null || discount.getStudentAge() != null || 
			(discount.getDiscountConcessionTypes() != null && !discount.getDiscountConcessionTypes().isEmpty()) || discount.getStudentPostcodes() != null
			|| (discount.getDiscountMembershipProducts() != null && !discount.getDiscountMembershipProducts().isEmpty());
	}
	
	public static List<DiscountCourseClass> getFilteredDiscounts(CourseClass courseClass) {
		List<DiscountCourseClass> classDiscountsWithoutCode = DiscountCourseClass.DISCOUNT.dot(Discount.CODE).isNull()
			.orExp(DiscountCourseClass.DISCOUNT.dot(Discount.CODE).eq(StringUtils.EMPTY)).filterObjects(getPotentialClassDiscounts(courseClass));
		List<DiscountCourseClass> classDiscounts = new ArrayList<>(classDiscountsWithoutCode.size());
		for (DiscountCourseClass classDiscount : classDiscountsWithoutCode) {
			if (hasAnyFiltering(classDiscount.getDiscount())) {
				classDiscounts.add(classDiscount);
			}
		}
		return classDiscounts;
	}
	
	@SuppressWarnings("unchecked")
	private static List<DiscountCourseClass> getPotentialClassDiscounts(CourseClass courseClass) {
		return ObjectSelect.query(DiscountCourseClass.class).where(DiscountCourseClass.COURSE_CLASS.eq(courseClass))
				.and(Discount.getCurrentDateFilterForDiscountCourseClass(courseClass.getStartDate())).select(courseClass.getObjectContext());
	}
	
}
