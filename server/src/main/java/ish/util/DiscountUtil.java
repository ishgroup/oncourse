/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.util;

import ish.common.types.ClassCostFlowType;
import ish.common.types.ClassCostRepetitionType;
import ish.messaging.ICourseClass;
import ish.oncourse.cayenne.DiscountCourseClassInterface;
import ish.oncourse.cayenne.DiscountInterface;
import ish.oncourse.server.cayenne.ClassCost;
import ish.oncourse.server.cayenne.CourseClass;
import ish.oncourse.server.cayenne.Discount;
import ish.oncourse.server.cayenne.DiscountCourseClass;
import ish.persistence.CommonExpressionFactory;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;

import java.util.Date;
import java.util.List;

public class DiscountUtil {


	/**
	 * Returns list of discounts applied by default localized in the given context.
	 */
	public static List<Discount> getDefaultDiscounts(ObjectContext context) {
		Date now = new Date();

		return ObjectSelect.query(Discount.class).where(ExpressionFactory.matchExp(Discount.ADD_BY_DEFAULT_PROPERTY, true))
				.and(ExpressionFactory.matchExp(Discount.VALID_FROM_PROPERTY, null).orExp(ExpressionFactory.lessOrEqualExp(Discount.VALID_FROM_PROPERTY, CommonExpressionFactory.previousMidnight(now))))
				.and(ExpressionFactory.matchExp(Discount.VALID_TO_PROPERTY, null).orExp(ExpressionFactory.greaterOrEqualExp(Discount.VALID_TO_PROPERTY, CommonExpressionFactory.nextMidnightMinusOne(now))))
				.select(context);

	}

	/**
	 * Checks if class is linked to the given discount.
	 */
	public static boolean hasDiscount(ICourseClass courseClass, DiscountInterface discount) {
		for (DiscountCourseClassInterface dcc : courseClass.getDiscountCourseClasses()) {
			if (discount.equals(dcc.getDiscount())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Links given class to the default discounts defined in the system.
	 */
	public static void assignDefaultDiscounts(CourseClass courseClass) {

		ObjectContext context = courseClass.getObjectContext();

		DiscountUtil.getDefaultDiscounts(context).stream()
				.filter(discount -> !DiscountUtil.hasDiscount(courseClass, discount))
				.forEach(discount -> {
			DiscountCourseClass dcc = context.newObject(DiscountCourseClass.class);
			dcc.setCourseClass(courseClass);
			dcc.setDiscount(discount);

			ClassCost cc = context.newObject(ClassCost.class);
			cc.setFlowType(ClassCostFlowType.DISCOUNT);
			cc.setRepetitionType(ClassCostRepetitionType.DISCOUNT);
			cc.setCourseClass(courseClass);
			cc.setDiscountCourseClass(dcc);
			cc.setDescription(discount.getName());
		});
	}

}
