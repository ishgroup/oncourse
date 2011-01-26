package ish.oncourse.enrol.components;

import ish.math.Country;
import ish.math.Money;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.Preference;
import ish.oncourse.model.RealDiscountsPolicy;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.preference.IPreferenceService;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class EnrolmentPrice {

	@Inject
	private IPreferenceService preferenceService;

	@Inject
	private IDiscountService discountService;

	@Parameter
	@Property
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

	private List<Discount> discounts;

	/**
	 * Initial setup of the component's instances. Formats; applicable discounts
	 * if the enrolment isn't invoiced and thus doesn't have discounts
	 * calculated.
	 */
	@SetupRender
	void beforeRender() {
		Preference currencyPref = preferenceService.getPreferenceByKey("default.currency");
		Locale locale = null;
		if (currencyPref != null) {
			locale = Country.forCurrencySymbol(currencyPref.getValueString()).locale();
		} else {
			locale = Country.AUSTRALIA.locale();
		}
		moneyFormat = NumberFormat.getCurrencyInstance(locale);
		numberFormat = new DecimalFormat("0.00");
		if (!isInvoiced()) {
			discounts = enrolment.getCourseClass()
					.getDiscountsToApply(
							new RealDiscountsPolicy(discountService.getPromotions(), enrolment
									.getStudent()));
		}
	}

	public String getFeeClass() {
		return "fee-" + (hasDiscountValue() ? "disabled" : "full");
	}

	public String getFeeDiscountedClass() {
		return "fee-discounted" + (!hasDiscountValue() ? " collapse" : "");
	}

	/**
	 * Checks if the discount value for the enrolment is greater than zero -
	 * returns true, otherwise false.
	 * 
	 * @return
	 */
	private boolean hasDiscountValue() {
		if (isInvoiced()) {
			return !enrolment.getInvoiceLine().getDiscountTotalExTax().isZero();
		} else {
			return !enrolment.getCourseClass().getDiscountAmountExTax(discounts).isZero();
		}

	}

	/**
	 * Discounted price of the enrolment including tax.
	 * 
	 * @return price
	 */
	public Money getDiscountedPriceIncTax() {
		if (isInvoiced()) {
			return enrolment.getInvoiceLine().getDiscountedPriceTotalIncTax();
		} else {
			return enrolment.getCourseClass().getDiscountedFeeIncTax(discounts);
		}
	}

	/**
	 * Discount amount of the enrolment including tax.
	 * 
	 * @return discount
	 */
	public Money getDiscountIncTax() {
		if (isInvoiced()) {
			return enrolment.getInvoiceLine().getDiscountTotalIncTax();
		} else {
			return enrolment.getCourseClass().getDiscountAmountIncTax(discounts);
		}
	}

	/**
	 * Checks if the {@link EnrolmentPrice#enrolment} has a not nul reference to
	 * the {@link InvoiceLine}.
	 * 
	 * @return
	 */
	private boolean isInvoiced() {
		return enrolment.getInvoiceLine() != null;
	}
}
