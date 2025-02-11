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

package ish.oncourse.server.cancel;

import ish.common.types.ConfirmationStatus;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.cayenne.InvoiceInterface;
import ish.oncourse.cayenne.PaymentInterface;
import ish.oncourse.cayenne.PaymentLineInterface;
import ish.oncourse.entity.services.InvoiceLineService;
import ish.oncourse.entity.services.SetPaymentMethod;
import ish.oncourse.server.cayenne.Account;
import ish.oncourse.server.cayenne.Enrolment;
import ish.oncourse.server.cayenne.Invoice;
import ish.oncourse.server.cayenne.InvoiceLine;
import ish.oncourse.server.cayenne.InvoiceLineDiscount;
import ish.oncourse.server.cayenne.InvoiceNoteRelation;
import ish.oncourse.server.cayenne.PaymentIn;
import ish.oncourse.server.cayenne.PaymentMethod;
import ish.oncourse.server.cayenne.SystemUser;
import ish.oncourse.server.cayenne.Tax;
import ish.util.InvoiceUtil;
import ish.util.MoneyUtil;
import ish.util.NoteUtil;

import ish.util.PaymentMethodUtil;
import org.apache.cayenne.validation.ValidationResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class for processing refunds. Now works only with vouchers but can be also used for enrolments and various products in future.
 */
public class RefundHelper {

    public static Invoice createRefundInvoice(InvoiceLine invoiceLineToRefund,
                                           Account account,
                                           Tax tax,
                                           Money cancellationFee,
                                           Boolean sendInvoice,
                                           ValidationResult result,
                                           String entityName,
                                           Boolean isTransfer,
                                           Boolean isEnrolmentCancellation,
                                           SystemUser systemUser) {

        var context = invoiceLineToRefund.getObjectContext();
        var localUser = context.localObject(systemUser);
        var invoiceToRefund = invoiceLineToRefund.getInvoice();

        var refundInvoice = context.newObject(Invoice.class);
        refundInvoice.setCreatedByUser(localUser);

        refundInvoice.setConfirmationStatus(sendInvoice ? ConfirmationStatus.NOT_SENT : ConfirmationStatus.DO_NOT_SEND);
        refundInvoice.setBillToAddress(invoiceToRefund.getBillToAddress());
        refundInvoice.setDescription(String.format("Refund for %s : %s", entityName, invoiceLineToRefund.getDescription()));
        NoteUtil.copyNotes(invoiceToRefund, refundInvoice, InvoiceNoteRelation.class);
        refundInvoice.setPublicNotes(invoiceToRefund.getPublicNotes());
        refundInvoice.setShippingAddress(invoiceToRefund.getShippingAddress());
        refundInvoice.setInvoiceDate(LocalDate.now());
        refundInvoice.setDateDue(LocalDate.now());
        refundInvoice.setDebtorsAccount(invoiceToRefund.getDebtorsAccount());
        refundInvoice.setContact(invoiceToRefund.getContact());

        createRefundInvoiceLine(invoiceLineToRefund, cancellationFee, tax, account, result, refundInvoice);

        InvoiceUtil.updateAmountOwing(invoiceToRefund);
        InvoiceUtil.updateAmountOwing(refundInvoice);

        if (!isTransfer) {

            var contraPayment = context.newObject(PaymentIn.class);
            contraPayment.setCreatedBy(localUser);
            contraPayment.setPayer(invoiceToRefund.getContact());
            SetPaymentMethod.valueOf(PaymentMethodUtil.getCONTRAPaymentMethods(context, PaymentMethod.class), contraPayment).set();
            contraPayment.setPaymentDate(LocalDate.now());
            contraPayment.setSource(PaymentSource.SOURCE_ONCOURSE);
            contraPayment.setAmount(Money.ZERO());
            contraPayment.setStatus(PaymentStatus.SUCCESS);


            if (isEnrolmentCancellation) {

                allocateMoneyToInvoices(Money.ZERO(), Arrays.asList(invoiceLineToRefund.getInvoice(), refundInvoice), contraPayment, new ArrayList<>());

            } else {

                // add all owing invoices for payer to allocation list to contra them with
                // credit amount just created from refund
                var invoices = new ArrayList<InvoiceInterface>(invoiceToRefund.getContact().getOwingInvoices());

                // make sure the refunded and refund invoice are in the list and this invoice is first
                // as this required for #18615 to balance the refunded invoice in first order:
                invoices.remove(invoiceToRefund);
                invoices.add(0, invoiceToRefund);

                if (!invoices.contains(refundInvoice)) {
                    invoices.add(refundInvoice);
                }

                // reverse GL records
                InvoiceUtil.allocateMoneyToInvoices(Money.ZERO(), invoices, contraPayment, new ArrayList<>());
            }
        }
        return refundInvoice;
    }

    public static void createRefundInvoiceLine(InvoiceLine invoiceLineToRefund, Money cancellationFee, Tax tax, Account account, ValidationResult result, Invoice refundInvoice) {
        var context = invoiceLineToRefund.getObjectContext();

        var refundInvoiceLine = context.newObject(InvoiceLine.class);

        refundInvoiceLine.setAccount(invoiceLineToRefund.getAccount()); // account from courseClass
        refundInvoiceLine.setPrepaidFeesAccount(invoiceLineToRefund.getPrepaidFeesAccount());
        refundInvoiceLine.setDescription(getRefundDescription(invoiceLineToRefund));
        refundInvoiceLine.setInvoice(refundInvoice);
        refundInvoiceLine.setSortOrder(invoiceLineToRefund.getSortOrder());
        refundInvoiceLine.setTitle(invoiceLineToRefund.getTitle());
        refundInvoiceLine.setUnit(invoiceLineToRefund.getUnit());
        refundInvoiceLine.setQuantity(new BigDecimal("1.00"));
        refundInvoiceLine.setTax(invoiceLineToRefund.getTax());
        refundInvoiceLine.setCourseClass(invoiceLineToRefund.getCourseClass());
        // when we create refund we should not recalculate tax each calculated on payment creation,
        // because of any applied on fly tax adjustment this value can be not the same
        refundInvoiceLine.setTaxEach(invoiceLineToRefund.getTaxEach().negate());
        // calculate the refund amount based on this enrolment paid price, so include the discount:
        refundInvoiceLine.setPriceEachExTax(invoiceLineToRefund.getPriceEachExTax().negate());
        refundInvoiceLine.setDiscountEachExTax(invoiceLineToRefund.getDiscountEachExTax().negate());


        for (InvoiceLineDiscount ilDiscount : invoiceLineToRefund.getInvoiceLineDiscounts()) {
            InvoiceLineDiscount invoiceLineDiscount = context.newObject(InvoiceLineDiscount.class);
            invoiceLineDiscount.setInvoiceLine(refundInvoiceLine);
            invoiceLineDiscount.setDiscount(ilDiscount.getDiscount());
        }

        if (cancellationFee != null && Money.ZERO().isLessThan(cancellationFee)) {
            var cancelaltionFeeLine = context.newObject(InvoiceLine.class);

            cancelaltionFeeLine.setAccount(account); // specified refund account
            cancelaltionFeeLine.setPrepaidFeesAccount(invoiceLineToRefund.getPrepaidFeesAccount());
            // this gives the description for class (including course name etc)
            cancelaltionFeeLine.setDescription(getRefundDescription(invoiceLineToRefund));
            cancelaltionFeeLine.setInvoice(refundInvoice);
            cancelaltionFeeLine.setSortOrder(invoiceLineToRefund.getSortOrder());
            cancelaltionFeeLine.setTitle("Cancellation fee");
            cancelaltionFeeLine.setUnit("");
            cancelaltionFeeLine.setQuantity(new BigDecimal("1.00"));
            if (tax != null) {
                cancelaltionFeeLine.setTax(context.localObject(tax));
            } else {
                throw new RuntimeException("no tax set");
            }
            cancelaltionFeeLine.setCourseClass(invoiceLineToRefund.getCourseClass());
            // calculate the refund amount based on this enrolment paid price, so include the discount:
            cancelaltionFeeLine.setPriceEachExTax(cancellationFee);
            cancelaltionFeeLine.setDiscountEachExTax(Money.ZERO());
            cancelaltionFeeLine.setTaxEach(recalculateTaxEach(cancellationFee, Money.ZERO(), tax.getRate()));
        }

        refundInvoice.validateForSave(result);
        refundInvoiceLine.validateForSave(result);
    }

    public static String getRefundDescription(InvoiceLine invoiceLineToRefund) {
        return String.format("Cancellation of %s: %s",
                invoiceLineToRefund.getEnrolment() != null ?
                        "enrolment" : "product",
                invoiceLineToRefund.getDescription());
    }

    //workaround of method from waCommon InvoiceUtil.allocateMoneyToInvoices
    //It was updated do not calculate all owingInvoices of contact since auto applying credit note to unpaid invoices disabled.
    private static Money allocateMoneyToInvoices(Money spendingMoney,
                                                 List<Invoice> invoices,
                                                 PaymentInterface payment,
                                                 List<PaymentLineInterface> paymentLines) {

        List<Invoice> owingInvoices = Invoice.AMOUNT_OWING.gt(Money.ZERO()).filterObjects(invoices);

        Money moneyRequiredForPayingAllInvoices = InvoiceUtil.sumInvoices(owingInvoices);
        if (invoices.size() > 0) {
            // go through the credit invoices first:
            for (InvoiceInterface anInvoice : invoices) {

                Money amountRequredToAllocate = spendingMoney.subtract(moneyRequiredForPayingAllInvoices);

                if (payment.getTypeOfPayment().equals(PaymentInterface.TYPE_IN) && anInvoice.getAmountOwing().compareTo(Money.ZERO()) < 0 &&
                        amountRequredToAllocate.compareTo(Money.ZERO()) < 0) {

                    Money allocatedAmount = InvoiceUtil.invoiceAllocate(anInvoice, amountRequredToAllocate, payment, paymentLines);
                    spendingMoney = spendingMoney.subtract(allocatedAmount);
                } else if (payment.getTypeOfPayment().equals(PaymentInterface.TYPE_OUT) && anInvoice.getAmountOwing().compareTo(Money.ZERO()) > 0 &&
                        amountRequredToAllocate.compareTo(Money.ZERO()) > 0) {
                    Money allocatedAmount = InvoiceUtil.invoiceAllocate(anInvoice, amountRequredToAllocate, payment, paymentLines);
                    spendingMoney = spendingMoney.subtract(allocatedAmount);
                }
            }
            // now go through the invoices and pay as much as possible
            for (InvoiceInterface anInvoice : invoices) {
                if (payment.getTypeOfPayment().equals(PaymentInterface.TYPE_IN) && anInvoice.getAmountOwing().compareTo(Money.ZERO()) > 0) {
                    Money allocatedAmount = InvoiceUtil.invoiceAllocate(anInvoice, spendingMoney, payment, paymentLines);
                    spendingMoney = spendingMoney.subtract(allocatedAmount);
                } else if (payment.getTypeOfPayment().equals(PaymentInterface.TYPE_OUT) && anInvoice.getAmountOwing().compareTo(Money.ZERO()) < 0) {
                    Money allocatedAmount = InvoiceUtil.invoiceAllocate(anInvoice, spendingMoney, payment, paymentLines);
                    spendingMoney = spendingMoney.subtract(allocatedAmount);
                }
            }
        }

        return spendingMoney;
    }

    private static Money recalculateTaxEach(Money priceEachEx, Money discountEachEx, BigDecimal taxRate) {
        // calculate final total value ex tax
        Money finalPriceEachEx = priceEachEx.subtract(discountEachEx);
        // calculate final total value inc tax
        Money priceEachInc = MoneyUtil.getPriceIncTax(priceEachEx, taxRate, null);
        Money discountEachInc = MoneyUtil.getPriceIncTax(discountEachEx, taxRate, Money.ZERO());
        Money finalPriceEachInc = priceEachInc.subtract(discountEachInc);
        // and finally taxEach
        Money taxEach = finalPriceEachInc.subtract(finalPriceEachEx);
        return taxEach;
    }
}

