/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.oncourse.cayenne.glue;

import ish.common.payable.EnrolmentInterface;
import ish.common.payable.IInvoiceLineInterface;
import ish.common.payable.InvoicePayableLineWrapper;
import ish.common.payable.TaxInterface;
import ish.math.Money;
import org.apache.cayenne.PersistentObject;

import java.math.BigDecimal;

/**
 * the superclass for the cayenne-generated _InvoiceLine. Includes the behavior implemented in InvoicePayableLineWrapper
 *
 */
public class InvoicePayableLine extends PersistentObject implements IInvoiceLineInterface {

	/**
	 *
	 */

	private transient InvoicePayableLineWrapper waPayableLine;

	public InvoicePayableLine() {
		this.waPayableLine = new InvoicePayableLineWrapper(this);
	}

	/**
	 * @see ish.common.payable.PayableLineInterface#getDiscountEachExTax()
	 */
	public Money getDiscountEachExTax() {
		return this.waPayableLine.getDiscountEachExTax();
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
	 * @see ish.common.payable.PayableLineInterface#getPriceEachExTax()
	 */
	public Money getPriceEachExTax() {
		return this.waPayableLine.getPriceEachExTax();
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
	 * @see ish.common.payable.PayableLineInterface#getTaxEach()
	 */
	public Money getTaxEach() {
		return this.waPayableLine.getTaxEach();
	}

	/**
	 * @see IInvoiceLineInterface#getDiscountedPriceTotalTax()
	 */
	public Money getDiscountedPriceTotalTax() {
		return this.waPayableLine.getDiscountedPriceTotalTax();
	}

	/**
	 * @see IInvoiceLineInterface#getDiscountTotalExTax()
	 */
	public Money getDiscountTotalExTax() {
		return this.waPayableLine.getDiscountTotalExTax();
	}

	/**
	 * @see IInvoiceLineInterface#getDiscountedPriceTotalIncTax()
	 */
	public Money getDiscountedPriceTotalIncTax() {
		return this.waPayableLine.getDiscountedPriceTotalIncTax();
	}

	/**
	 * @see IInvoiceLineInterface#getDiscountedTaxOnPriceEach()
	 */
	public Money getDiscountedTaxOnPriceEach() {
		return this.waPayableLine.getDiscountedTaxOnPriceEach();
	}

	/**
	 * @see IInvoiceLineInterface#setQuantity(BigDecimal)
	 */
	public void setQuantity(BigDecimal quantity) { waPayableLine.setQuantity(quantity);	}

	/**
	 * @see IInvoiceLineInterface#getQuantity()
	 */
	public BigDecimal getQuantity() {
		return this.waPayableLine.getQuantity();
	}

	/**
	 * @see IInvoiceLineInterface#getInvoiceTax()
	 */
	public TaxInterface getInvoiceTax() {
		return this.waPayableLine.getInvoiceTax();
	}

	/**
	 * @see IInvoiceLineInterface#getDiscountedPriceTotalExTax()
	 */
	public Money getDiscountedPriceTotalExTax() {
		return this.waPayableLine.getDiscountedPriceTotalExTax();
	}

	/**
	 * @see IInvoiceLineInterface#getTotalTax()
	 */
	public Money getTotalTax() {
		return this.waPayableLine.getTotalTax();
	}

	/**
	 * @see IInvoiceLineInterface#setDiscountEachExTax(Money)
	 */
	public void setDiscountEachExTax(Money discountEachExTax) {
		this.waPayableLine.setDiscountEachExTax(discountEachExTax);
	}

	/**
	 * @see IInvoiceLineInterface#getFinalPriceToPayIncTax()
	 */
	public Money getFinalPriceToPayIncTax() {
		return this.waPayableLine.getFinalPriceToPayIncTax();
	}

	/**
	 * @see IInvoiceLineInterface#getFinalPriceToPayExTax()
	 */
	public Money getFinalPriceToPayExTax() {
		return this.waPayableLine.getFinalPriceToPayExTax();
	}

	/**
	 * @see IInvoiceLineInterface#getEnrolment()
	 */
	public EnrolmentInterface getEnrolment() {
		return waPayableLine.getEnrolment();
	}

	/**
	 * @see IInvoiceLineInterface#setPriceEachExTax(Money)
	 */
	public void setPriceEachExTax(Money priceEachExTax) {
		waPayableLine.setPriceEachExTax(priceEachExTax);
	}

	/**
	 * @see IInvoiceLineInterface#setTaxEach(Money)
	 */
	public void setTaxEach(Money taxEach) {
		waPayableLine.setTaxEach(taxEach);
	}

}
