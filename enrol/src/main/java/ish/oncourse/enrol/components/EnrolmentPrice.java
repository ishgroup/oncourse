package ish.oncourse.enrol.components;

import ish.math.Money;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;

import java.text.DecimalFormat;
import java.text.Format;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class EnrolmentPrice {

	@Parameter
	private Enrolment enrolment;

	@Parameter
	@Property
	private CourseClass courseClass;

	@Parameter
	@Property
	private int index;

	@Property
	private Format moneyFormat;

	@Property
	private Format numberFormat;

	@SetupRender
	void beforeRender() {
		moneyFormat = new DecimalFormat("#,##0.00");
		numberFormat = new DecimalFormat("0.00");
	}

	public String getFeeClass() {
		return "fee-" + (hasDiscountValue() ? "disabled" : "full");
	}

	public String getFeeDiscountedClass() {
		return "fee-discounted" + (!hasDiscountValue() ? " collapse" : "");
	}

	private boolean hasDiscountValue() {
		if (enrolment.getDiscount() == null) {
			return false;
		}
		return Money.ZERO.toBigDecimal().compareTo(
				enrolment.getDiscount().getDiscountAmount()) != 0;
	}

}
