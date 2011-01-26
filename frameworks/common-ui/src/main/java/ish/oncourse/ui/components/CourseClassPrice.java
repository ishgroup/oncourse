package ish.oncourse.ui.components;

import ish.math.Money;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.PotentialDiscountsPolicy;
import ish.oncourse.services.discount.IDiscountService;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class CourseClassPrice {

	@Inject
	private IDiscountService discountService;

	@Parameter
	@Property
	private CourseClass courseClass;

	private Format feeFormat;

	@Persist
	@Property
	private Money discountedFee;

	@Property
	private Discount discountItem;

	@Persist
	@Property
	private List<Discount> discountsWithConcessions;

	@Persist
	@Property
	private Money discountValue;

	private List<Discount> applicableDiscounts;

	@SetupRender
	public void beforeRender() {
		this.feeFormat = new DecimalFormat("#,##0.00");
		List<Discount> promotions = discountService.getPromotions();
		applicableDiscounts = courseClass.getDiscountsToApply(new PotentialDiscountsPolicy(
				promotions));
		discountedFee = courseClass.getDiscountedFeeIncTax(applicableDiscounts);
		discountValue = courseClass.getDiscountAmountIncTax(applicableDiscounts);
		discountsWithConcessions = courseClass.getConcessionDiscounts();
	}

	public boolean isHasFee() {
		return courseClass.getFeeExGst() != null;
	}

	public Format getFeeFormat() {
		return this.feeFormat;
	}

	public boolean isHasDiscountValue() {
		return !discountValue.isZero();
	}

	public boolean isTaxExempt() {
		return courseClass.isGstExempt();
	}

	public Money getDiscountItemFeeIncTax() {
		ArrayList<Discount> disc = new ArrayList<Discount>(1);
		disc.add(discountItem);
		return courseClass.getDiscountedFeeIncTax(disc);
	}

}
