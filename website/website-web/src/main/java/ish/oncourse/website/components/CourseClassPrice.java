package ish.oncourse.website.components;

import ish.oncourse.math.Money;
import ish.oncourse.model.CourseClass;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class CourseClassPrice {

	@Parameter
	@Property
	private CourseClass courseClass;

	
	private Format feeFormat;

	@SetupRender
	public void beforeRender() {
		this.feeFormat = new DecimalFormat("#,##0.00");
	}

	public boolean isHasFee() {
		return courseClass.getFeeExGst() != null;
	}

	public Format getFeeFormat() {
		return this.feeFormat;
	}

	public boolean isHasDiscountValue() {
		return Money.ZERO.toBigDecimal().compareTo(getDiscountValue()) != 0;
	}

	public boolean isTaxExempt() {
		return false;
	}

	public BigDecimal getDiscountedFee() {
		return new BigDecimal(0);
	}

	public BigDecimal getDiscountValue() {
		// if ( this.discountValue == null )
		// {
		// if ( hasObject() )
		// {
		// LOG.debug( "hasClass for price" );
		// this.discountValue = Discount.discountValueForCourseClass(
		// discounts(), getObject() );
		// }
		// if ( this.discountValue == null )
		// {
		// this.discountValue = Money.ZERO.toBigDecimal();
		// }
		// LOG.debug( "discounted value:" + this.discountValue );
		// }
		// return this.discountValue;

		return new BigDecimal(0);
	}
}
