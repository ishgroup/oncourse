/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.model;

import ish.common.payable.EnrolmentInterface;
import ish.common.payable.IInvoiceLineInterface;
import ish.common.payable.InvoicePayableLineWrapper;
import ish.common.payable.TaxInterface;
import ish.math.Money;
import org.apache.cayenne.CayenneDataObject;

import java.math.BigDecimal;

/**
 * the superclass for the cayenne-generated _InvoiceLine. Includes the behavior implemented in InvoicePayableLineWrapper
 * 
 * @author ksenia
 */
public class InvoicePayableLine extends CayenneDataObject implements IInvoiceLineInterface {
	private static final long serialVersionUID = 7857794479347463855L;
	private InvoicePayableLineWrapper waPayableLine;

	public InvoicePayableLine() {
		waPayableLine=new InvoicePayableLineWrapper(this);
	}

	/**
	 * @see ish.common.payable.PayableLineInterface#getDiscountEachExTax()
	 */
	public Money getDiscountEachExTax() {
		return waPayableLine.getDiscountEachExTax();
	}

	/**
	 * @see ish.common.payable.PayableLineInterface#getDiscountEachIncTax()
	 */
	public Money getDiscountEachIncTax() {
		return waPayableLine.getDiscountEachIncTax();
	}

	/**
	 * @see ish.common.payable.PayableLineInterface#getDiscountTotalIncTax()
	 */
	public Money getDiscountTotalIncTax() {
		return waPayableLine.getDiscountTotalIncTax();
	}

	/**
	 * @see ish.common.payable.PayableLineInterface#getPriceEachExTax()
	 */
	public Money getPriceEachExTax() {
		return waPayableLine.getPriceEachExTax();
	}

	/**
	 * @see ish.common.payable.PayableLineInterface#getPriceTotalExTax()
	 */
	public Money getPriceTotalExTax() {
		return waPayableLine.getPriceTotalExTax();
	}

	/**
	 * @see ish.common.payable.PayableLineInterface#getPriceEachIncTax()
	 */
	public Money getPriceEachIncTax() {
		return waPayableLine.getPriceEachIncTax();
	}

	/**
	 * @see ish.common.payable.PayableLineInterface#getPriceTotalIncTax()
	 */
	public Money getPriceTotalIncTax() {
		return waPayableLine.getPriceTotalIncTax();
	}

	/**
	 * @see ish.common.payable.PayableLineInterface#getTaxEach()
	 */
	public Money getTaxEach() {
		return waPayableLine.getTaxEach();
	}

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getDiscountedPriceTotalTax()
	 */
	public Money getDiscountedPriceTotalTax() {
		return waPayableLine.getDiscountedPriceTotalTax();
	}

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getDiscountTotalExTax()
	 */
	public Money getDiscountTotalExTax() {
		return waPayableLine.getDiscountTotalExTax();
	}

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getDiscountedPriceTotalIncTax()
	 */
	public Money getDiscountedPriceTotalIncTax() {
		return waPayableLine.getDiscountedPriceTotalIncTax();
	}

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getDiscountedTaxOnPriceEach()
	 */
	public Money getDiscountedTaxOnPriceEach() {
		return waPayableLine.getDiscountedTaxOnPriceEach();
	}

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getQuantity()
	 */
	public BigDecimal getQuantity() {
		return waPayableLine.getQuantity();
	}

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getInvoiceTax()
	 */
	public TaxInterface getInvoiceTax() {
		return waPayableLine.getInvoiceTax();
	}

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getDiscountedPriceTotalExTax()
	 */
	public Money getDiscountedPriceTotalExTax() {
		return waPayableLine.getDiscountedPriceTotalExTax();
	}

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getTotalTax()
	 */
	public Money getTotalTax() {
		return waPayableLine.getTotalTax();
	}

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#setDiscountEachExTax(ish.math.Money)
	 */
	public void setDiscountEachExTax(Money discountEachExTax) {
		waPayableLine.setDiscountEachExTax(discountEachExTax);
	}

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getFinalPriceToPayIncTax()
	 */
	public Money getFinalPriceToPayIncTax() {
		return waPayableLine.getFinalPriceToPayIncTax();
	}

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getFinalPriceToPayExTax()
	 */
	public Money getFinalPriceToPayExTax() {
		return waPayableLine.getFinalPriceToPayExTax();
	}
	
	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getEnrolment()
	 */
	public EnrolmentInterface getEnrolment() {
		return waPayableLine.getEnrolment();
	}

	@Override
	public void setPriceEachExTax(Money priceExTax) {
		waPayableLine.setPriceEachExTax(priceExTax);
	}

	@Override
	public void setTaxEach(Money taxEach) {
		waPayableLine.setTaxEach(taxEach);		
	}

}
