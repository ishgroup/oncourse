package ish.oncourse.util.payment;

import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.math.Money;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.utils.PaymentInUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class PaymentInAbandon {
    private static final Logger logger = LogManager.getLogger();

    private PaymentInModel model;

    private Set<PaymentIn> refundPayments = new HashSet<>();

    private PaymentInAbandon() {
    }

    public PaymentInAbandon perform()
    {
        switch (model.getPaymentIn().getStatus()) {
            case FAILED:
            case FAILED_CARD_DECLINED:
            case FAILED_NO_PLACES:
                break;
            default:
                model.getPaymentIn().setStatus(PaymentStatus.FAILED);
        }

        for (PaymentIn voucherPayment : model.getRelatedVoucherPayments()) {
            if (!PaymentStatus.STATUSES_FINAL.contains(voucherPayment.getStatus())) {
                PaymentInUtil.reverseVoucherPayment(voucherPayment);
            }
        }

        Date today = new Date();
        model.getPaymentIn().setModified(today);

        for (Invoice invoice : model.getInvoices()) {
            invoice.updateAmountOwing();
            addRefundInvoice(invoice);
        }
        return this;
    }

    /**
     * The method creates one REVERSE payment and two payment lines
     * to relate direct and reverse invoice and to balance amount owing.
     * One of payment lines is linked to direct invoice and has the same amount as direct invoice,
     * other one is linked to reverse invoice and has negative amount.
     */
    private void addRefundInvoice(Invoice invoiceToRefund) {
        invoiceToRefund.updateAmountOwing();

        List<PaymentInLine> paymentInLinesToRefund = new ArrayList<>(invoiceToRefund.getPaymentInLines());

        //if the owing already balanced, no reason to create any refund invoice
        if (!Money.isZeroOrEmpty(invoiceToRefund.getAmountOwing()) &&
                paymentInLinesToRefund.size() > 0) {
            // Creating refund invoice
            Invoice refundInvoice = invoiceToRefund.createRefundInvoice();
            logger.info(String.format("Created refund invoice with amount:%s for invoice:%s.", refundInvoice.getAmountOwing(),
                    invoiceToRefund.getId()));

            PaymentIn internalPayment = model.getPaymentIn().makeShallowCopy();
            internalPayment.setAmount(Money.ZERO);
            internalPayment.setType(PaymentType.REVERSE);
            internalPayment.setStatus(PaymentStatus.SUCCESS);
            internalPayment.setSessionId(model.getPaymentIn().getSessionId());

            PaymentInLine refundPL = internalPayment.getObjectContext().newObject(PaymentInLine.class);
            refundPL.setAmount(Money.ZERO.subtract(invoiceToRefund.getTotalGst()));
            refundPL.setCollege(internalPayment.getCollege());
            refundPL.setInvoice(refundInvoice);
            refundPL.setPaymentIn(internalPayment);

            PaymentInLine paymentInLineToRefundCopy = internalPayment.getObjectContext().newObject(PaymentInLine.class);
            paymentInLineToRefundCopy.setAmount(invoiceToRefund.getTotalGst());
            paymentInLineToRefundCopy.setCollege(internalPayment.getCollege());
            paymentInLineToRefundCopy.setInvoice(invoiceToRefund);
            paymentInLineToRefundCopy.setPaymentIn(internalPayment);

            invoiceToRefund.setModified(model.getPaymentIn().getModified());
            refundPayments.add(internalPayment);
        }

        for (PaymentInLine paymentInLine : paymentInLinesToRefund) {
            refundPayments.add(paymentInLine.getPaymentIn());
        }

        // Fail enrollments on invoiceToRefund
        PaymentInUtil.makeFail(invoiceToRefund.getInvoiceLines());
    }

    public Set<PaymentIn> getRefundPayments() {
        return refundPayments;
    }


    public static PaymentInAbandon valueOf(PaymentInModel model)
    {
        PaymentInAbandon paymentInAbandon = new PaymentInAbandon();
        paymentInAbandon.model = model;
        return paymentInAbandon;
    }
}
