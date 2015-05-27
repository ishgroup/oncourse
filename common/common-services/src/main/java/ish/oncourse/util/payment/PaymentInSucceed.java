package ish.oncourse.util.payment;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceDueDate;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.VoucherPaymentIn;
import ish.oncourse.utils.PaymentInUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

/**
 * Sets the status of payment to {@link PaymentStatus#SUCCESS}, and sets the
 * success statuses to the related invoice and enrolment ( {@link EnrolmentStatus#SUCCESS} ).
 *
 * Invoked when the payment gateway processing is succeed.
 *
 */
public class PaymentInSucceed {
    private static final Logger logger = LogManager.getLogger();

    private PaymentInModel model;

    private PaymentInSucceed() {
    }

    public PaymentInSucceed perform() {
        model.getPaymentIn().setStatus(PaymentStatus.SUCCESS);

        // succeed all related voucher payments
        for (PaymentIn voucherPayment : model.getVoucherPayments()) {
            if (!PaymentStatus.STATUSES_FINAL.contains(voucherPayment.getStatus())) {
                voucherPayment.setStatus(PaymentStatus.SUCCESS);

                //we need the code to be sure that all entities which a related to
                // the voucher payment are added to the replication.
                List<VoucherPaymentIn> voucherPaymentIns = voucherPayment.getVoucherPaymentIns();
                for (VoucherPaymentIn voucherPaymentIn : voucherPaymentIns) {
                    voucherPaymentIn.setModified(new Date());
                }
                voucherPayment.getVoucher().setModified(new Date());
            }
        }

        Date today = new Date();

        for (Invoice invoice: model.getInvoices()) {
            invoice.setModified(today);
            //the code needs to be sure that invoiceDueDates will be pit to the replication
            for (InvoiceDueDate invoiceDueDate: invoice.getInvoiceDueDates()) {
                invoiceDueDate.setModified(new Date());
            }
            PaymentInUtil.makeSuccess(invoice.getInvoiceLines());
        }
       return this;
    }

    public static PaymentInSucceed valueOf(PaymentInModel model)
    {
        PaymentInSucceed paymentInSucceed = new PaymentInSucceed();
        paymentInSucceed.model = model;
        return paymentInSucceed;
    }
}
