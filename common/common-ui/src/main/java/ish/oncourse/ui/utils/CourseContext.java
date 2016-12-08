/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.utils;

import ish.oncourse.model.Discount;
import ish.oncourse.services.courseclass.CheckClassAge;

import java.util.List;

public class CourseContext {
	private List<Discount> discounts;
	private CheckClassAge  checkClassAge;

	public static CourseContext valueOf(List<Discount> discounts, CheckClassAge  checkClassAge) {
		CourseContext context = new CourseContext();
		context.discounts = discounts;
		context.checkClassAge = checkClassAge;
		return context;
	}
	
	public CheckClassAge getCheckClassAge() {
		return checkClassAge;
	}

	public List<Discount> getDiscounts() {
		return discounts;
	}
}
