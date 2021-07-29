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

package ish.oncourse.server.cayenne

import ish.math.Money
import ish.oncourse.API
import ish.oncourse.server.cayenne.glue._InvoiceLine
import ish.util.InvoiceUtil
import ish.validation.ValidationFailure
import org.apache.cayenne.Cayenne
import org.apache.cayenne.query.ObjectIdQuery
import org.apache.cayenne.validation.ValidationResult

import javax.annotation.Nonnull


@API
class InvoiceLine extends _InvoiceLine {

    @Override
    Invoice getInvoice() {
        return super.getInvoice()
    }

    @Override
    Class<Invoice> getInvoicePersistentClass() {
        return Invoice.class
    }

    @Override
    void setInvoice(AbstractInvoice abstractInvoice) {
        super.setInvoice((Invoice) abstractInvoice)
    }

    /**
     * Check if async replication is allowed on this object.
     *
     * @return isAsyncReplicationAllowed
     */
    @Override
    boolean isAsyncReplicationAllowed() {

        // If invoice is not yet linked to any payments.
        Enrolment enrol = getEnrolment()
        if (enrol != null) {
            ObjectIdQuery q = new ObjectIdQuery(enrol.getObjectId(), false, ObjectIdQuery.CACHE_REFRESH)
            enrol = (Enrolment) Cayenne.objectForQuery(getObjectContext(), q)
            if (!enrol.isAsyncReplicationAllowed()) {
                return false
            }
        }
        return getInvoice().isAsyncReplicationAllowed()
    }

    /**
     * @return account linked to this invoice line
     */
    @Nonnull
    @API
    @Override
    Account getAccount() {
        return super.getAccount()
    }

    /**
     * @return class linked to this invoice line
     */
    @Nonnull
    @API
    @Override
    CourseClass getCourseClass() {
        return super.getCourseClass()
    }

    /**
     * @return enrolment linked to this invoice line
     */
    @Nonnull
    @API
    @Override
    Enrolment getEnrolment() {
        return super.getEnrolment()
    }

    @Override
    void setEnrolment(Enrolment enrolment) {
        super.setEnrolment(enrolment)
    }

    /**
     * @return list of discounts linked to this invoice line
     */
    @Nonnull
    @API
    @Override
    List<InvoiceLineDiscount> getInvoiceLineDiscounts() {
        return super.getInvoiceLineDiscounts()
    }

    /**
     * @return product items linked to this invoice line
     */
    @Nonnull
    @API
    @Override
    List<ProductItem> getProductItems() {
        return super.getProductItems()
    }

    /**
     * @return voucher payment records linked to this invoice line
     */
    @Nonnull
    @API
    @Override
    List<VoucherPaymentIn> getVoucherPaymentIn() {
        return super.getVoucherPaymentIn()
    }

    @Override
    void validateForSave(@Nonnull ValidationResult result) {
        super.validateForSave(result)

        if (getPrepaidFeesRemaining() == null) {
            // prepaid fees cannot be null
            result.addFailure(ValidationFailure.validationFailure(this, PREPAID_FEES_REMAINING_PROPERTY,
                    "The invoice line must have a prepaid fees remaining."))
        } else if (getEnrolment() == null && getCourseClass() == null && !getPrepaidFeesRemaining().isZero()) {
            result.addFailure(ValidationFailure.validationFailure(this, PREPAID_FEES_REMAINING_PROPERTY,
                    "The prepaid fees remaining must be zero for invoice line without enrolment."))
        } else if (getPrepaidFeesRemaining().isGreaterThan(Money.ZERO) &&
                getPrepaidFeesRemaining().isGreaterThan(getFinalPriceToPayExTax())) {
            // value of prepaid fees cannot be greater than absolute value of the invoice total ex tax
            result.addFailure(ValidationFailure.validationFailure(this, PREPAID_FEES_REMAINING_PROPERTY,
                    "The prepaid fees remaining cannot be greater than invoice total ex tax."))
        }
    }

    void recalculatePrepaidFeesRemaining() {
        if (enrolment != null || courseClass != null) {
            prepaidFeesRemaining = finalPriceToPayExTax
        } else {
            prepaidFeesRemaining = Money.ZERO
        }
    }

    void recalculateTaxEach() {
        if (tax) {
            Money localPriceEachExTax = priceEachExTax
            Money localDiscountEachExTax = discountEachExTax
            if (localPriceEachExTax == null) {
                localPriceEachExTax = Money.ZERO
            }
            if (localDiscountEachExTax == null) {
                localDiscountEachExTax = Money.ZERO;
            }
            Money taxAdjustment = Money.ZERO;
            if (enrolment != null && enrolment.courseClass != null && !Money.isZeroOrEmpty(enrolment.courseClass.taxAdjustment)) {
                taxAdjustment = taxAdjustment.add(enrolment.courseClass.taxAdjustment)
            }
            for (ProductItem item : getProductItems()) {
                if (item != null && item.getProduct() != null && !Money.isZeroOrEmpty(item.product.taxAdjustment)) {
                    taxAdjustment = taxAdjustment.add(item.product.taxAdjustment)
                }
            }
            setTaxEach(InvoiceUtil.calculateTaxEachForInvoiceLine(localPriceEachExTax, localDiscountEachExTax, tax.rate, taxAdjustment))
        }
    }

}
