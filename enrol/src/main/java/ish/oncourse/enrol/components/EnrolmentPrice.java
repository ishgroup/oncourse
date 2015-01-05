package ish.oncourse.enrol.components;

import ish.math.Country;
import ish.math.Money;
import ish.oncourse.model.Application;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.RealDiscountsPolicy;
import ish.oncourse.services.application.IApplicationService;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.util.FormatUtils;

import java.math.BigDecimal;
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
	private PreferenceController preferenceService;

	@Inject
	private IDiscountService discountService;
	
	@Inject
	private IApplicationService applicationService;

	@Parameter
	@Property
	private Enrolment enrolment;

	@Parameter
	@Property
	private CourseClass courseClass;

	@Parameter
	@Property
	private String index;

	@Property
	private Format moneyFormat;

	@Property
	private Format numberFormat;

	private List<Discount> discounts;
	
	private Application application;

	/**
	 * Initial setup of the component's instances. Formats; applicable discounts
	 * if the enrolment isn't invoiced and thus doesn't have discounts
	 * calculated.
	 */
	@SetupRender
	void beforeRender() {

		Country country = preferenceService.getCountry();
		Locale locale = (country != null) ? country.locale() : Country.AUSTRALIA.locale();

		numberFormat = new DecimalFormat("0.00");
		application = applicationService.findOfferedApplicationBy(enrolment.getCourseClass().getCourse(), enrolment.getStudent());
		if (!isOverriden() && !isInvoiced()) {
			discounts = enrolment.getCourseClass().getDiscountsToApply(
					new RealDiscountsPolicy(discountService.getPromotions(), enrolment.getStudent()));
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
			Money discountedTotalExTax = Money.ZERO;
			for (InvoiceLine invoiceLine : enrolment.getInvoiceLines()) {
				discountedTotalExTax = discountedTotalExTax.add(invoiceLine.getDiscountTotalExTax());
			}
			return !discountedTotalExTax.isZero();
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
		Money discountedPriceIncTax = Money.ZERO;
		if (isInvoiced()) {
			for (InvoiceLine invoiceLine : enrolment.getInvoiceLines()) {
				discountedPriceIncTax = discountedPriceIncTax.add(invoiceLine.getDiscountedPriceTotalIncTax());
			}
		} else {
			discountedPriceIncTax = enrolment.getCourseClass().getDiscountedFeeIncTax(discounts);
		}
		moneyFormat = FormatUtils.chooseMoneyFormat(discountedPriceIncTax);
		return discountedPriceIncTax;
	}

	/**
	 * Discount amount of the enrolment including tax.
	 * 
	 * @return discount
	 */
	public Money getDiscountIncTax() {
		Money discountIncTax = Money.ZERO;
		if (isInvoiced()) {
			for (InvoiceLine invoiceLine : enrolment.getInvoiceLines()) {
				discountIncTax = discountIncTax.add(invoiceLine.getDiscountTotalIncTax());
			}
		} else {
			discountIncTax = enrolment.getCourseClass().getDiscountAmountIncTax(discounts);
		}
		moneyFormat = FormatUtils.chooseMoneyFormat(discountIncTax);
		return discountIncTax;
	}

	/**
	 * Checks if the {@link EnrolmentPrice#enrolment} has a not nul reference to
	 * the {@link InvoiceLine}.
	 * 
	 * @return
	 */
	private boolean isInvoiced() {
		return !enrolment.getInvoiceLines().isEmpty() && enrolment.getOriginalInvoiceLine() != null;
	}

	public Money getFee() {
		Money feeIncGst = courseClass.getFeeIncGst();
		moneyFormat = FormatUtils.chooseMoneyFormat(feeIncGst);
		return feeIncGst;
	}
	
	public boolean isOverriden() {
		return application != null && application.getFeeOverride() != null;
	}

	public Money getFeeOverride() {
		Money feeOverride = application.getFeeOverride();
		moneyFormat = FormatUtils.chooseMoneyFormat(feeOverride);
		if (courseClass.isGstExempt()) {
			return feeOverride;
		} else {
			return feeOverride.multiply(BigDecimal.ONE.add(courseClass.getTaxRate()));
		}
	}
}
