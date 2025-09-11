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

package ish.common.payable;

import ish.math.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Wrapper that implements the behavior of InvoiceLine-specific methods
 */
public class InvoicePayableLineWrapper implements IInvoiceLineInterface {
    private static final long serialVersionUID = 1L;
    /**
     * Wrapped instance for reading/writing basic cayenne-model properties
     */
    private IInvoiceLineInterface invoiceLine;

    public InvoicePayableLineWrapper(IInvoiceLineInterface instance) {
        this.invoiceLine = instance;
    }

    public Money getDiscountTotalExTax() {
        if (getQuantity() == null || getDiscountEachExTax() == null) {
            return Money.ZERO;
        }
        return Money.of(getQuantity()).multiply(getDiscountEachExTax());
    }

    public Money getDiscountEachIncTax() {
        if (getDiscountEachExTax() == null || Money.ZERO.equals(getDiscountEachExTax()) || getInvoiceTax() == null) {
            setDiscountEachExTax(Money.ZERO);
            return Money.ZERO;
        }
        Money priceEachExTax = getPriceEachExTax();
        if (priceEachExTax == null)
            priceEachExTax = Money.ZERO;

        Money discountEachExTax = getDiscountEachExTax();
        Money taxEach = getTaxEach();

        Money priceEachIncTax = getPriceEachIncTax();
        Money discounted = priceEachExTax.subtract(discountEachExTax).add(taxEach);
        return priceEachIncTax.subtract(discounted);
    }

    public Money getDiscountTotalIncTax() {
        if (getDiscountEachExTax() == null || Money.ZERO.equals(getDiscountEachExTax()) || getQuantity() == null) {
            return Money.ZERO;
        }
        return getDiscountEachIncTax().multiply(getQuantity());
    }

    public Money getPriceTotalExTax() {
        if (getQuantity() == null || getPriceEachExTax() == null) {
            return Money.ZERO;
        }
        return Money.of(getQuantity()).multiply(getPriceEachExTax());
    }

    public Money getPriceEachIncTax() {
        if (getPriceEachExTax() == null || getInvoiceTax() == null) {
            return Money.ZERO;
        }
        return getPriceEachExTax().add(getDiscountedTaxOnPriceEach());
    }

    public Money getPriceTotalIncTax() {
        if (getQuantity() == null || getPriceEachExTax() == null) {
            return Money.ZERO;
        }
        return Money.of(getQuantity()).multiply(getPriceEachIncTax());
    }

    public Money getDiscountedPriceTotalTax() {
        return getDiscountedPriceTotalIncTax().subtract(getDiscountedPriceTotalExTax());
    }

    public Money getDiscountedPriceTotalIncTax() {
        // == (getPriceEachExTax() ?:0)*quantity  - (getDiscountEachExTax() ?:0)*quantity + getTaxEach() * quantity
        return getDiscountedPriceTotalExTax().add(getTotalTax());
    }

    public Money getDiscountedTaxOnPriceEach() {
        if (getPriceEachExTax() == null || getInvoiceTax() == null || getPriceEachExTax().equals(getDiscountEachExTax())) {
            return Money.ZERO;
        } else if (getDiscountEachExTax() == null || getDiscountEachExTax().equals(Money.ZERO)) {
            return getTaxEach();
        }
        return Money.of(getPriceEachExTax().toBigDecimal()
                .divide(getPriceEachExTax().subtract(getDiscountEachExTax()).toBigDecimal(), 10, RoundingMode.HALF_EVEN).multiply(getTaxEach().toBigDecimal()));
    }

    public Money getDiscountedPriceTotalExTax() {
        return getPriceTotalExTax().subtract(getDiscountTotalExTax());
    }

    public Money getTotalTax() {
        if (getQuantity() == null || getTaxEach() == null || getInvoiceTax() == null) {
            return Money.ZERO;
        }
        return getTaxEach().multiply(getQuantity());
    }

    // use the implementation of cayenne-generated entities

    public Money getTaxEach() {
        return this.invoiceLine.getTaxEach();
    }

    @Override
    public void setTaxEach(Money taxEach) {
        invoiceLine.setTaxEach(taxEach);
    }

    public BigDecimal getQuantity() {
        return this.invoiceLine.getQuantity();
    }

    public void setQuantity(BigDecimal quantity) {
        this.invoiceLine.setQuantity(quantity);
    }

    public Money getDiscountEachExTax() {
        return this.invoiceLine.getDiscountEachExTax();
    }

    public void setDiscountEachExTax(Money discountEachExTax) {
        this.invoiceLine.setDiscountEachExTax(discountEachExTax);
    }

    @Override
    public TaxInterface getTax() {
        return invoiceLine.getTax();
    }

    // this method is supposed to be overridden in InvoiceLine and return "getTax()"

    public Money getPriceEachExTax() {
        return this.invoiceLine.getPriceEachExTax();
    }

    @Override
    public void setPriceEachExTax(Money priceEachExTax) {
        invoiceLine.setPriceEachExTax(priceEachExTax);
    }

    public TaxInterface getInvoiceTax() {
        return this.invoiceLine.getInvoiceTax();
    }

    public Money getFinalPriceToPayIncTax() {
        var discountExTax = getDiscountEachExTax();
        var taxRate = invoiceLine.getTax().getRate();
        var priceExTax = getPriceEachExTax();
        if(priceExTax == null)
            return Money.ZERO;

        var discountedPriceExTax = priceExTax.subtract(discountExTax != null ? discountExTax : Money.ZERO);
        var tax = discountedPriceExTax.multiply(taxRate);
        return Money.of(discountedPriceExTax.add(tax).toBigDecimal());
    }

    public Money getFinalPriceToPayExTax() {
        return getPriceTotalExTax().subtract(getDiscountTotalExTax());
    }

    @Override
    public EnrolmentInterface getEnrolment() {
        return invoiceLine.getEnrolment();
    }
}
