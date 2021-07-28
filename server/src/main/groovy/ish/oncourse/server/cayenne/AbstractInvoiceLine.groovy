/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import ish.common.payable.IInvoiceLineInterface
import ish.common.payable.InvoicePayableLineWrapper
import ish.common.payable.TaxInterface
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._AbstractInvoiceLine
import ish.util.AccountUtil
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable


/**
 * Includes the behavior implemented in InvoicePayableLineWrapper
 */
@API
@QueueableEntity
abstract class AbstractInvoiceLine extends _AbstractInvoiceLine implements IInvoiceLineInterface, Queueable {

    private transient InvoicePayableLineWrapper waPayableLine

    public static final String TOTAL_PRICE_INC_TAX_PROPERTY = "total_price";
    public static final String TOTAL_PRICE_EX_TAX_PROPERTY = "total_price_ex_tax";
    public static final String DISCOUNT_EX_TAX_PROPERTY = "discount_applied_ex_tax";

    private static final Logger logger = LogManager.getLogger(InvoiceLine.class)

    abstract <T extends AbstractInvoice> T getInvoice()

    abstract void setInvoice(AbstractInvoice abstractInvoice)

    abstract CourseClass getCourseClass()

    abstract void setCourseClass(CourseClass courseClass)

    abstract Enrolment getEnrolment()

    abstract void setEnrolment(Enrolment enrolment)

    abstract List<InvoiceLineDiscount> getInvoiceLineDiscounts()


    AbstractInvoiceLine() {
        this.waPayableLine = new InvoicePayableLineWrapper(this)
    }

    /**
     * @return discount amount including tax per single item on this invoice line
     */
    @API
    @Override
    Money getDiscountEachIncTax() {
        return this.waPayableLine.getDiscountEachIncTax()
    }

    /**
     * @return total discounted amount including tax for all items on this invoice line (discount for single item times quantity)
     */
    @API
    @Override
    Money getDiscountTotalIncTax() {
        return this.waPayableLine.getDiscountTotalIncTax()
    }

    /**
     * @return total price (price per single item times quantity) without discount excluding tax
     */
    @API
    @Override
    Money getPriceTotalExTax() {
        return this.waPayableLine.getPriceTotalExTax()
    }

    /**
     * @return price per single item without discounts including tax
     */
    @API
    @Override
    Money getPriceEachIncTax() {
        return this.waPayableLine.getPriceEachIncTax()
    }

    /**
     * @return total price (price per single item times quantity) without discount including tax
     */
    @API
    @Override
    Money getPriceTotalIncTax() {
        return this.waPayableLine.getPriceTotalIncTax()
    }

    /**
     * @return total tax amount calculated by subtracting total discounted price excluding tax from total discounted price including tax
     */
    @API

    Money getDiscountedPriceTotalTax() {
        return this.waPayableLine.getDiscountedPriceTotalTax()
    }

    /**
     * @return total discount amount (discount per single item times quantity) excluding tax
     */
    @API
    @Override
    Money getDiscountTotalExTax() {
        return this.waPayableLine.getDiscountTotalExTax()
    }

    /**
     * @return total price (price per single item times quantity) including discount and including tax
     */
    @API
    @Override
    Money getDiscountedPriceTotalIncTax() {
        return this.waPayableLine.getDiscountedPriceTotalIncTax()
    }

    /**
     * @return discounted tax amount per single item on this invoice line
     */
    @API
    @Override
    Money getDiscountedTaxOnPriceEach() {
        return this.waPayableLine.getDiscountedTaxOnPriceEach()
    }

    /**
     * @return total price (price per single item times quantity) including discount and excluding tax
     */
    @API
    @Override
    Money getDiscountedPriceTotalExTax() {
        return this.waPayableLine.getDiscountedPriceTotalExTax()
    }

    /**
     * @return list of discounts linked to this invoice line
     */
    @Nonnull @API
    List<Discount> getDiscounts() {
        List<Discount> discounts = new ArrayList<>()
        for (InvoiceLineDiscount id : getInvoiceLineDiscounts()) {
            discounts.add(id.getDiscount())
        }
        return discounts
    }

    /**
     * @return total tax amount (tax amount per single item times quantity)
     */
    @API
    @Override
    Money getTotalTax() {
        return this.waPayableLine.getTotalTax()
    }

    /**
     * @return final price for this invoice line including tax
     */
    @API
    @Override
    Money getFinalPriceToPayIncTax() {
        return this.waPayableLine.getFinalPriceToPayIncTax()
    }

    /**
     * @return final price for this invoice line excluding tax
     */
    @API
    @Override
    Money getFinalPriceToPayExTax() {
        return this.waPayableLine.getFinalPriceToPayExTax()
    }

    /**
     * @return the of tax rate expressing found by dividing the tax amount by the total price
     */
    @API
    float getTaxRate() {
        if (getPriceEachExTax() == null || getPriceEachExTax().isZero()) {
            return getTax().getRate().floatValue()
        }
        float taxEachFloat = getTaxEach().floatValue()
        float priceEachExTax = getPriceEachExTax().floatValue()
        float discountEach = getDiscountEachExTax().floatValue()
        return taxEachFloat / (priceEachExTax - discountEach)
    }

    @Override
    void onEntityCreation() {
        super.onEntityCreation()
        if (getPrepaidFeesRemaining() == null) {
            setPrepaidFeesRemaining(getFinalPriceToPayExTax())
        }
        if (getPrepaidFeesAccount() == null) {
            setPrepaidFeesAccount(AccountUtil.getDefaultPrepaidFeesAccount(getObjectContext(), Account.class))
        }
    }

    /**
     * @return prepaid fees liability account
     */
    @Nonnull
    @API
    @Override
    Account getPrepaidFeesAccount() {
        return super.getPrepaidFeesAccount()
    }

    /**
     * @return the date and time this record was created
     */
    @API
    @Override
    Date getCreatedOn() {
        return super.getCreatedOn()
    }

    /**
     * @return invoice line description
     */
    @API
    @Override
    String getDescription() {
        return super.getDescription()
    }

    /**
     * @return discount amount excluding tax per single item on this invoice line
     */
    @Nonnull
    @API
    @Override
    Money getDiscountEachExTax() {
        return super.getDiscountEachExTax()
    }


    /**
     * @return the date and time this record was modified
     */
    @API
    @Override
    Date getModifiedOn() {
        return super.getModifiedOn()
    }

    /**
     * @return remaining amount on prepaid fees liability account which is not yet transferred to income account
     */
    @Nonnull
    @API
    @Override
    Money getPrepaidFeesRemaining() {
        return super.getPrepaidFeesRemaining()
    }

    /**
     * @return price per single item without discounts excluding tax
     */
    @Nonnull
    @API
    @Override
    Money getPriceEachExTax() {
        return super.getPriceEachExTax()
    }

    /**
     * @return quantity of items
     */
    @Nonnull
    @API
    @Override
    BigDecimal getQuantity() {
        return super.getQuantity()
    }

    /**
     * Invoice lines ordering number in invoice. This is needed to properly process reverse invoices
     * coming from willow. See {@link ish.oncourse.server.lifecycle.InvoiceLineInitHelper}
     */
    @Override
    Integer getSortOrder() {
        return super.getSortOrder()
    }

    /**
     * @return COS account linked to this invoice line
     */
    @API
    @Nullable
    @Override
    Account getCosAccount() {
        return super.getCosAccount()
    }

    void setTax(Tax tax) {
        super.setTax(tax)
    }

    /**
     * @return tax linked to this invoice line
     */
    @Nonnull
    @API
    @Override
    Tax getTax() {
        return super.getTax()
    }

    @Override
    TaxInterface getInvoiceTax() {
        return super.getTax()
    }

    /**
     * @return tax amount per single item
     */
    @Nonnull
    @API
    @Override
    Money getTaxEach() {
        return super.getTaxEach()
    }

    /**
     * @return invoice line title
     */
    @Nonnull
    @API
    @Override
    String getTitle() {
        return super.getTitle()
    }

    /**
     * @return quantity unit of measurement (e.g. kg)
     */
    @API
    @Override
    String getUnit() {
        return super.getUnit()
    }

    @Override
    String getSummaryDescription() {
        return getTitle()
    }

    @Override
    void setDiscountEachExTax(Money discountEachEx) {
        if (getTax() == null) {
            throw new IllegalStateException("You must set the tax rate before setting the discount.")
        }

        super.setDiscountEachExTax(discountEachEx)

        if (getInvoice() != null) {
            getInvoice().updateAmountOwing()
        }
    }

    @Override
    void setPriceEachExTax(Money priceEachExTax) {
        if (getTax() == null) {
            throw new IllegalStateException("You must set the tax rate before setting the price.")
        }

        super.setPriceEachExTax(priceEachExTax)

        if (getInvoice() != null) {
            getInvoice().updateAmountOwing()
        }
    }

    void setDiscountEachIncTax(Money discount) {
        if (discount == null || discount > Money.ZERO || tax == null) {
            discountEachExTax = Money.ZERO
        } else {
            discountEachExTax = discount.divide(BigDecimal.ONE + tax.rate)
        }
        if (discountEachExTax != null && tax != null) {
            if (discountEachExTax != Money.ZERO) {
                if (discount == null) {
                    throw new IllegalStateException("Not 0 discounteach founded for 0 discount")
                }
                Money taxAdjustment = (discountEachExTax.multiply(BigDecimal.ONE + tax.rate)).subtract(discount)
                if (taxAdjustment != Money.ZERO) {
                    taxEach = taxEach.add(taxAdjustment)
                }
            }
        }
    }
}
