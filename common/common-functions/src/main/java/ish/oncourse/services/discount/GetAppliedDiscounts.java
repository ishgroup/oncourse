/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.discount;

import ish.oncourse.model.*;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

import java.math.BigDecimal;
import java.util.List;

public class GetAppliedDiscounts {

	private List<Discount> promotions;
	private CourseClass courseClass;
	private BigDecimal taxRate = null;

	
	public static GetAppliedDiscounts valueOf(CourseClass courseClass, List<Discount> promotions, BigDecimal taxRate) {
		GetAppliedDiscounts get = new GetAppliedDiscounts();
		get.courseClass = courseClass;
		get.promotions = promotions;
		get.taxRate = taxRate;

		return get;
	}
	
	public List<DiscountCourseClass> get() {
		List<DiscountCourseClass> discounts =  ObjectSelect.query(DiscountCourseClass.class).
				where(DiscountCourseClass.COURSE_CLASS.eq(courseClass)).
				and(DiscountCourseClass.DISCOUNT.dot(Discount.HIDE_ON_WEB).isFalse()).
				and(DiscountCourseClass.DISCOUNT.dot(Discount.STUDENT_ENROLLED_WITHIN_DAYS).isNull()).
				and(DiscountCourseClass.DISCOUNT.dot(Discount.STUDENT_AGE).isNull()).
				and(DiscountCourseClass.DISCOUNT.dot(Discount.STUDENT_POSTCODES).isNull()).
				and(DiscountCourseClass.DISCOUNT.dot(Discount.LIMIT_PREVIOUS_ENROLMENT).isFalse()).
				and(DiscountCourseClass.DISCOUNT.dot(Discount.MIN_ENROLMENTS).lte(1)).
				and(DiscountCourseClass.DISCOUNT.dot(Discount.MIN_VALUE).lte(courseClass.getFeeIncGst(taxRate))).
				and(DiscountCourseClass.DISCOUNT.dot(Discount.DISCOUNT_CONCESSION_TYPES).outer().dot(DiscountConcessionType.CONCESSION_TYPE).isNull()).
				and(DiscountCourseClass.DISCOUNT.dot(Discount.CORPORATE_PASS_DISCOUNTS).outer().dot(CorporatePassDiscount.CREATED).isNull()).
				and(DiscountCourseClass.DISCOUNT.dot(Discount.DISCOUNT_MEMBERSHIP_PRODUCTS).outer().dot(DiscountMembership.CREATED).isNull()).
				and(DiscountCourseClass.DISCOUNT.dot(Discount.ENTITY_RELATION_TYPES).outer().dot(EntityRelationType.CREATED).isNull()).

				and(
						DiscountCourseClass.DISCOUNT.dot(Discount.CODE).isNull()
								.orExp(DiscountCourseClass.DISCOUNT.dot(Discount.CODE).isNotNull()
										.andExp(DiscountCourseClass.DISCOUNT.in(promotions)))
				).
				prefetch(DiscountCourseClass.DISCOUNT.joint()).
				cacheStrategy(QueryCacheStrategy.LOCAL_CACHE).
				cacheGroup(DiscountCourseClass.class.getSimpleName()).
				select(courseClass.getObjectContext());
		
		return WebDiscountUtils.filterValidDateRange(discounts, courseClass.getStartDate());
	}
}
