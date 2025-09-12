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
package ish.oncourse.cayenne.glue

import ish.common.payable.EnrolmentInterface
import ish.common.payable.IInvoiceLineInterface
import ish.common.payable.InvoicePayableLineWrapper
import ish.common.payable.TaxInterface
import ish.math.Money
import org.apache.cayenne.PersistentObject

/**
 * the superclass for the cayenne-generated _InvoiceLine. Includes the behavior implemented in InvoicePayableLineWrapper
 *
 */
class InvoicePayableLine extends PersistentObject implements IInvoiceLineInterface {

    private transient InvoicePayableLineWrapper waPayableLine

    InvoicePayableLine() {
        this.waPayableLine = new InvoicePayableLineWrapper(this)
    }

    /**
     * @see ish.common.payable.PayableLineInterface#getDiscountEachExTax()
     */
    Money getDiscountEachExTax() {
        return this.waPayableLine.getDiscountEachExTax()
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
     * @see ish.common.payable.PayableLineInterface#getPriceEachExTax()
     */
    Money getPriceEachExTax() {
        return this.waPayableLine.getPriceEachExTax()
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
     * @see ish.common.payable.PayableLineInterface#getTaxEach()
     */
    Money getTaxEach() {
        return this.waPayableLine.getTaxEach()
    }

    /**
     * @see IInvoiceLineInterface#getDiscountedPriceTotalTax()
     */
    Money getDiscountedPriceTotalTax() {
        return this.waPayableLine.getDiscountedPriceTotalTax()
    }

    /**
     * @see IInvoiceLineInterface#getDiscountTotalExTax()
     */
    Money getDiscountTotalExTax() {
        return this.waPayableLine.getDiscountTotalExTax()
    }

    /**
     * @see IInvoiceLineInterface#getDiscountedPriceTotalIncTax()
     */
    Money getDiscountedPriceTotalIncTax() {
        return this.waPayableLine.getDiscountedPriceTotalIncTax()
    }

    /**
     * @see IInvoiceLineInterface#getDiscountedTaxOnPriceEach()
     */
    Money getDiscountedTaxOnPriceEach() {
        return this.waPayableLine.getDiscountedTaxOnPriceEach()
    }

    /**
     * @see IInvoiceLineInterface#setQuantity(BigDecimal)
     */
    void setQuantity(BigDecimal quantity) { waPayableLine.setQuantity(quantity) }

    /**
     * @see IInvoiceLineInterface#getQuantity()
     */
    BigDecimal getQuantity() {
        return this.waPayableLine.getQuantity()
    }

    /**
     * @see IInvoiceLineInterface#getInvoiceTax()
     */
    TaxInterface getInvoiceTax() {
        return this.waPayableLine.getInvoiceTax()
    }

    /**
     * @see IInvoiceLineInterface#getDiscountedPriceTotalExTax()
     */
    Money getDiscountedPriceTotalExTax() {
        return this.waPayableLine.getDiscountedPriceTotalExTax()
    }

    /**
     * @see IInvoiceLineInterface#getTotalTax()
     */
    Money getTotalTax() {
        return this.waPayableLine.getTotalTax()
    }

    /**
     * @see IInvoiceLineInterface#setDiscountEachExTax(Money)
     */
    void setDiscountEachExTax(Money discountEachExTax) {
        this.waPayableLine.setDiscountEachExTax(discountEachExTax)
    }

    /**
     * @see IInvoiceLineInterface#getFinalPriceToPayIncTax()
     */
    Money getFinalPriceToPayIncTax() {
        return this.waPayableLine.getFinalPriceToPayIncTax()
    }

    /**
     * @see IInvoiceLineInterface#getFinalPriceToPayExTax()
     */
    Money getFinalPriceToPayExTax() {
        return this.waPayableLine.getFinalPriceToPayExTax()
    }

    /**
     * @see IInvoiceLineInterface#getEnrolment()
     */
    EnrolmentInterface getEnrolment() {
        return waPayableLine.getEnrolment()
    }

    /**
     * @see IInvoiceLineInterface#setPriceEachExTax(Money)
     */
    void setPriceEachExTax(Money priceEachExTax) {
        waPayableLine.setPriceEachExTax(priceEachExTax)
    }

    /**
     * @see IInvoiceLineInterface#setTaxEach(Money)
     */
    void setTaxEach(Money taxEach) {
        waPayableLine.setTaxEach(taxEach)
    }

}
