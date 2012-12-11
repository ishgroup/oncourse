package ish.oncourse.utils;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;

import java.util.Arrays;
import java.util.Comparator;

public class DiscountFeeComparator implements Comparator<Discount>{
	private CourseClass courseClass;
	
	public DiscountFeeComparator(CourseClass courseClass) {
		this.courseClass = courseClass;
	}

	@Override
	public int compare(Discount discount1, Discount discount2) {
		if (discount1 == null || discount2 == null) {
			return 0;
		}
		return courseClass.getDiscountedFeeIncTax(Arrays.asList(discount2)).compareTo(courseClass.getDiscountedFeeIncTax(Arrays.asList(discount1)));
	}

}
