/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.discount;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountConcessionType;
import ish.oncourse.model.DiscountCourseClass;
import ish.oncourse.model.DiscountMembership;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

import java.util.List;

public class GetPossibleDiscounts {

	private  CourseClass courseClass;

	public static GetPossibleDiscounts valueOf(CourseClass courseClass) {
		GetPossibleDiscounts get = new GetPossibleDiscounts();
		get.courseClass = courseClass;
		return get;
	}
	
	public List<DiscountCourseClass> get() {
		List<DiscountCourseClass> discounts = ObjectSelect.query(DiscountCourseClass.class).
				where(DiscountCourseClass.COURSE_CLASS.eq(courseClass)).
				and(DiscountCourseClass.DISCOUNT.dot(Discount.CODE).isNull()).
				and(DiscountCourseClass.DISCOUNT.dot(Discount.HIDE_ON_WEB).isFalse()).
				and(DiscountCourseClass.DISCOUNT.dot(Discount.IS_AVAILABLE_ON_WEB).isTrue()).
				and(
						DiscountCourseClass.DISCOUNT.dot(Discount.STUDENT_ENROLLED_WITHIN_DAYS).isNotNull().
								orExp(DiscountCourseClass.DISCOUNT.dot(Discount.STUDENT_AGE).isNotNull()).
								orExp(DiscountCourseClass.DISCOUNT.dot(Discount.STUDENT_POSTCODES).isNotNull()).
								orExp(DiscountCourseClass.DISCOUNT.dot(Discount.DISCOUNT_MEMBERSHIP_PRODUCTS).outer().dot(DiscountMembership.CREATED).isNotNull()).
								orExp(DiscountCourseClass.DISCOUNT.dot(Discount.DISCOUNT_CONCESSION_TYPES).outer().dot(DiscountConcessionType.CONCESSION_TYPE).isNotNull())
				).
				prefetch(DiscountCourseClass.DISCOUNT.joint()).
				cacheStrategy(QueryCacheStrategy.LOCAL_CACHE).
				cacheGroups(DiscountCourseClass.class.getSimpleName()).
				select(courseClass.getObjectContext());
		
		return WebDiscountUtils.filterValidDateRange(discounts,courseClass.getStartDate());
	}
}
