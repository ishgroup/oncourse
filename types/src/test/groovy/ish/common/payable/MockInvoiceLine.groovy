package ish.common.payable

import groovy.transform.CompileStatic
import ish.math.Money

@CompileStatic
class MockInvoiceLine implements IInvoiceLineInterface {

	private transient InvoicePayableLineWrapper waPayableLine
    Money priceEachEx
    Money discountEachEx
    Money taxEach
    BigDecimal quantity
    TaxInterface tax

    MockInvoiceLine(Money priceEachEx, Money discountEachEx, BigDecimal quantity, final BigDecimal taxRate) {
		this.priceEachEx = priceEachEx
        this.discountEachEx = discountEachEx
        this.quantity = quantity
        this.waPayableLine = new InvoicePayableLineWrapper(this)

        if (taxRate != null) {
			this.tax = new TaxInterface() {
				String getDescription() {
					return "mock tax with rate " + taxRate + " (" + hashCode() + ")"
                }
			}
        }

		Money result = Money.ZERO
        if (getPriceEachExTax() != null) {
			result = result.add(getPriceEachExTax())
        }

		if (getDiscountEachExTax() != null) {
			result = result.subtract(getDiscountEachExTax())
        }
		if (taxRate != null) {
			result = result.multiply(taxRate)
        }

		this.taxEach = result

    }

	/**
	 * @see ish.common.payable.PayableLineInterface#getDiscountEachIncTax()
	 */
    Money getDiscountEachIncTax() {
		return this.waPayableLine.getDiscountEachIncTax()
    }

	/**
	 * @see ish.common.payable.PayableLineInterface#getDiscountTotalIncTax()
	 */
    Money getDiscountTotalIncTax() {
		return this.waPayableLine.getDiscountTotalIncTax()
    }

	/**
	 * @see ish.common.payable.PayableLineInterface#getPriceTotalExTax()
	 */
    Money getPriceTotalExTax() {
		return this.waPayableLine.getPriceTotalExTax()
    }

	/**
	 * @see ish.common.payable.PayableLineInterface#getPriceEachIncTax()
	 */
    Money getPriceEachIncTax() {
		return this.waPayableLine.getPriceEachIncTax()
    }

	/**
	 * @see ish.common.payable.PayableLineInterface#getPriceTotalIncTax()
	 */
    Money getPriceTotalIncTax() {
		return this.waPayableLine.getPriceTotalIncTax()
    }

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getDiscountedPriceTotalTax()
	 */
    Money getDiscountedPriceTotalTax() {
		return this.waPayableLine.getDiscountedPriceTotalTax()
    }

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getDiscountTotalExTax()
	 */
    Money getDiscountTotalExTax() {
		return this.waPayableLine.getDiscountTotalExTax()
    }

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getDiscountedPriceTotalIncTax()
	 */
    Money getDiscountedPriceTotalIncTax() {
		return this.waPayableLine.getDiscountedPriceTotalIncTax()
    }

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getDiscountedTaxOnPriceEach()
	 */
    Money getDiscountedTaxOnPriceEach() {
		return this.waPayableLine.getDiscountedTaxOnPriceEach()
    }

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getDiscountedPriceTotalExTax()
	 */
    Money getDiscountedPriceTotalExTax() {
		return this.waPayableLine.getDiscountedPriceTotalExTax()
    }

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getTotalTax()
	 */
    Money getTotalTax() {
		return this.waPayableLine.getTotalTax()
    }

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getInvoiceTax()
	 */
    TaxInterface getInvoiceTax() {
		return this.tax
    }

    void setDiscountEachExTax(Money discountEachExTax) {
		this.discountEachEx = discountEachExTax
    }

    Money getPriceEachExTax() {
		return this.priceEachEx
    }

    Money getDiscountEachExTax() {
		return this.discountEachEx
    }

    Money getTaxEach() {
		return this.taxEach
    }

    BigDecimal getQuantity() {
		return this.quantity
    }

	@Override
    void setQuantity(BigDecimal quantity) {
		this.quantity = quantity
    }

    Money getFinalPriceToPayIncTax() {
		return getPriceTotalIncTax().subtract(getDiscountTotalIncTax())
    }

    Money getFinalPriceToPayExTax() {
		return getPriceTotalExTax().subtract(getDiscountTotalExTax())
    }

	@Override
    EnrolmentInterface getEnrolment() {
		return waPayableLine.getEnrolment()
    }

	@Override
    void setPriceEachExTax(Money priceEachExTax) {
		this.priceEachEx = priceEachExTax
    }

	@Override
    void setTaxEach(Money taxEach) {
		this.taxEach = taxEach
    }
}
