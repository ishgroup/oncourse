package ish.oncourse.ui.components;

import ish.math.Money;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountCourseClass;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;


public class CourseClassPrice {

	@Parameter
	@Property
	private CourseClass courseClass;

	private Format feeFormat;

	@Persist
	private BigDecimal discountedFee;

	@Property
	private Discount discountItem;

	@Persist
	private List<Discount> discountsWithConcessions;

	@Persist
	private BigDecimal discountValue;

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
		return courseClass.isGstExempt();
	}

	public BigDecimal getDiscountedFee() {
		if (discountedFee == null) {
			discountedFee = courseClass.getFeeExGst().subtract(
					new Money(getDiscountValue()))
					.multiply(courseClass.getTaxMultiplier()).toBigDecimal();

			if (discountedFee == null) {
				discountedFee = Money.ZERO.toBigDecimal();
			}
		}
		return discountedFee;
	}

	public BigDecimal getDiscountValue() {
		if (this.discountValue == null) {
			this.discountValue = Discount.discountValueForCourseClass(
					getDiscounts(), courseClass);

			if (this.discountValue == null) {
				this.discountValue = Money.ZERO.toBigDecimal();
			}

		}
		return this.discountValue;
	}

	/**
	 * @return the list of promotion discounts
	 */
	private List<Discount> getDiscounts() {
		// TODO get discounts from request
		// myApplication().discountsForRequest( context().request(),
		// EOSharedEditingContext.defaultSharedEditingContext() );
		return new ArrayList<Discount>();
	}

	/**
	 * @return all the Discounts that apply to this CourseClass, but only if
	 *         they have one or more concessions
	 */
	public List<Discount> getDiscountsWithConcessions() {
		if (this.discountsWithConcessions == null) {
			List<Discount> discounts = new ArrayList<Discount>();
			for (DiscountCourseClass dcc : courseClass.getDiscountCourseClasses()) {
				if (dcc.getDiscount() == null
						|| dcc.getDiscount().getDiscountConcessionTypes() == null) {
					continue;
				}
				if (dcc.getDiscount().getDiscountConcessionTypes().size() > 0
						&& !discounts.contains(dcc.getDiscount())) {
					discounts.add(dcc.getDiscount());
				}
			}
			this.discountsWithConcessions = discounts;
		}

		if (this.discountsWithConcessions == null) {
			this.discountsWithConcessions = new ArrayList<Discount>();
		}

		return this.discountsWithConcessions;
	}

	public BigDecimal getDiscountItemFeeIncTax() {
		if (discountItem == null) {
			return BigDecimal.ZERO;
		}
		ArrayList<Discount> discountsList = new ArrayList<Discount>();
		discountsList.add(discountItem);
		return courseClass.getFeeExGst()
				.subtract(new Money(
						Discount.discountValueForCourseClass(discountsList,
								courseClass))).multiply(
						courseClass.getTaxMultiplier()).toBigDecimal();
	}

}
