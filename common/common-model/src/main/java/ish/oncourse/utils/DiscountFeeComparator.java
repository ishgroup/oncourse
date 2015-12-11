package ish.oncourse.utils;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountCourseClass;

import java.util.Arrays;
import java.util.Comparator;

public class DiscountFeeComparator implements Comparator<DiscountCourseClass>{
	private CourseClass courseClass;
	
	public DiscountFeeComparator(CourseClass courseClass) {
		this.courseClass = courseClass;
	}

	@Override
	public int compare(DiscountCourseClass discount1, DiscountCourseClass discount2) {
		if (discount1 == null || discount2 == null) {
			return 0;
		}
		return courseClass.getDiscountedFeeIncTax(discount2).compareTo(courseClass.getDiscountedFeeIncTax(discount2));
	}

}
