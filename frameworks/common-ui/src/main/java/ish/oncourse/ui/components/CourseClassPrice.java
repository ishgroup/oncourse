package ish.oncourse.ui.components;

import ish.math.Money;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
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
		applicableDiscounts = discountService.chooseBestDiscountsVariant(getDiscounts(),
				courseClass);
		discountedFee = discountService.discountedValueForList(applicableDiscounts,
				courseClass.getFeeIncGst());
		discountValue = discountService.discountValueForList(applicableDiscounts,
				courseClass.getFeeIncGst());
		discountsWithConcessions = discountService.getConcessionDiscounts(courseClass);
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

	/**
	 * @return the list of promotion discounts
	 */
	private List<Discount> getDiscounts() {
		// TODO get discounts from request
		// myApplication().discountsForRequest( context().request(),
		// EOSharedEditingContext.defaultSharedEditingContext() );
		return new ArrayList<Discount>();
	}

	public Money getDiscountItemFeeIncTax() {
		ArrayList<Discount> disc = new ArrayList<Discount>(1);
		disc.add(discountItem);
		return discountService.discountedValueForList(disc, courseClass.getFeeExGst());
	}

}
