
package ish.oncourse.enrol.components;

import ish.math.Country;
import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.services.application.IApplicationService;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.Locale;

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

	@Parameter
	@Property
	private Discount discount;

	private BigDecimal taxRate;
	
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
		Tax taxOverride =  enrolment.getInvoiceLines().get(0).getInvoice().getContact().getTaxOverride();
		taxRate = taxOverride == null ? null : taxOverride.getRate();
		numberFormat = new DecimalFormat("0.00");
		application = applicationService.findOfferedApplicationBy(enrolment.getCourseClass().getCourse(), enrolment.getStudent());
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
			return discount != null && !enrolment.getCourseClass().getDiscountAmountExTax(discount, taxRate).isZero();
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
		} else if (discount != null) {
			discountedPriceIncTax = enrolment.getCourseClass().getDiscountedFeeIncTax(enrolment.getCourseClass().getDiscountCourseClassBy(discount), taxRate);
		} else {
			discountedPriceIncTax =  enrolment.getCourseClass().getFeeIncGst(taxRate);
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
		} else if (discount != null) {
			discountIncTax = enrolment.getCourseClass().getDiscountAmountIncTax(enrolment.getCourseClass().getDiscountCourseClassBy(discount), taxRate);
		} else {
			discountIncTax = Money.ZERO;
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
		Money feeIncGst = courseClass.getFeeIncGst(taxRate);
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
			return feeOverride.multiply(BigDecimal.ONE.add(taxRate != null ? taxRate : courseClass.getTaxRate()));
		}
	}
}
