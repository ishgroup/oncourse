package ish.common.payable;

import ish.math.Money;

import java.math.BigDecimal;

public class MockInvoiceLine implements IInvoiceLineInterface {

	private static final long serialVersionUID = 1L;
	private transient InvoicePayableLineWrapper waPayableLine;
	Money priceEachEx;
	Money discountEachEx;
	Money taxEach;
	BigDecimal quantity;
	TaxInterface tax;

	public MockInvoiceLine(Money priceEachEx, Money discountEachEx, BigDecimal quantity, final BigDecimal taxRate) {
		this.priceEachEx = priceEachEx;
		this.discountEachEx = discountEachEx;
		this.quantity = quantity;
		this.waPayableLine = new InvoicePayableLineWrapper(this);

		if (taxRate != null) {
			this.tax = new TaxInterface() {
				public String getDescription() {
					return "mock tax with rate " + taxRate + " (" + hashCode() + ")";
				}
			};
		}

		Money result = Money.ZERO;
		if (getPriceEachExTax() != null) {
			result = result.add(getPriceEachExTax());
		}

		if (getDiscountEachExTax() != null) {
			result = result.subtract(getDiscountEachExTax());
		}
		if (taxRate != null) {
			result = result.multiply(taxRate);
		}

		this.taxEach = result;

	}

	/**
	 * @see ish.common.payable.PayableLineInterface#getDiscountEachIncTax()
	 */
	public Money getDiscountEachIncTax() {
		return this.waPayableLine.getDiscountEachIncTax();
	}

	/**
	 * @see ish.common.payable.PayableLineInterface#getDiscountTotalIncTax()
	 */
	public Money getDiscountTotalIncTax() {
		return this.waPayableLine.getDiscountTotalIncTax();
	}

	/**
	 * @see ish.common.payable.PayableLineInterface#getPriceTotalExTax()
	 */
	public Money getPriceTotalExTax() {
		return this.waPayableLine.getPriceTotalExTax();
	}

	/**
	 * @see ish.common.payable.PayableLineInterface#getPriceEachIncTax()
	 */
	public Money getPriceEachIncTax() {
		return this.waPayableLine.getPriceEachIncTax();
	}

	/**
	 * @see ish.common.payable.PayableLineInterface#getPriceTotalIncTax()
	 */
	public Money getPriceTotalIncTax() {
		return this.waPayableLine.getPriceTotalIncTax();
	}

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getDiscountedPriceTotalTax()
	 */
	public Money getDiscountedPriceTotalTax() {
		return this.waPayableLine.getDiscountedPriceTotalTax();
	}

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getDiscountTotalExTax()
	 */
	public Money getDiscountTotalExTax() {
		return this.waPayableLine.getDiscountTotalExTax();
	}

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getDiscountedPriceTotalIncTax()
	 */
	public Money getDiscountedPriceTotalIncTax() {
		return this.waPayableLine.getDiscountedPriceTotalIncTax();
	}

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getDiscountedTaxOnPriceEach()
	 */
	public Money getDiscountedTaxOnPriceEach() {
		return this.waPayableLine.getDiscountedTaxOnPriceEach();
	}

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getDiscountedPriceTotalExTax()
	 */
	public Money getDiscountedPriceTotalExTax() {
		return this.waPayableLine.getDiscountedPriceTotalExTax();
	}

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getTotalTax()
	 */
	public Money getTotalTax() {
		return this.waPayableLine.getTotalTax();
	}

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getInvoiceTax()
	 */
	public TaxInterface getInvoiceTax() {
		return this.tax;
	}

	public void setDiscountEachExTax(Money discountEachExTax) {
		this.discountEachEx = discountEachExTax;
	}

	public Money getPriceEachExTax() {
		return this.priceEachEx;
	}

	public Money getDiscountEachExTax() {
		return this.discountEachEx;
	}

	public Money getTaxEach() {
		return this.taxEach;
	}

	public BigDecimal getQuantity() {
		return this.quantity;
	}

	public Money getFinalPriceToPayIncTax() {
		return getPriceTotalIncTax().subtract(getDiscountTotalIncTax());
	}

	public Money getFinalPriceToPayExTax() {
		return getPriceTotalExTax().subtract(getDiscountTotalExTax());
	}

	@Override
	public EnrolmentInterface getEnrolment() {
		return waPayableLine.getEnrolment();
	}

	@Override
	public void setPriceEachExTax(Money priceEachExTax) {
		this.priceEachEx = priceEachExTax;
	}

	@Override
	public void setTaxEach(Money taxEach) {
		this.taxEach = taxEach;
	}
}
