package ish.oncourse.util.payment;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.InvoiceLineDiscount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

/**
 * Fails payment, but does not override state if already FAILED. Refreshes
 * all the statuses of dependent entities to allow user to reuse them.
 */
public class PaymentInFail {
    private static final Logger logger = LogManager.getLogger();

    private PaymentInModel model;

    private PaymentInFail() {
    }

    public PaymentInFail perform() {

        switch (model.getPaymentIn().getStatus()) {
            case FAILED:
            case FAILED_CARD_DECLINED:
            case FAILED_NO_PLACES:
                break;
            default:
                model.getPaymentIn().setStatus(PaymentStatus.FAILED);
        }

        Date today = new Date();

        for (Invoice invoice: model.getInvoices()) {
            invoice.setModified(today);
            for (InvoiceLine il : invoice.getInvoiceLines()) {
                il.setModified(today);
                for (InvoiceLineDiscount ilDiscount : il.getInvoiceLineDiscounts()) {
                    ilDiscount.setModified(today);
                }
                Enrolment enrol = il.getEnrolment();
                if (enrol != null) {
                    enrol.setModified(today);
                    if (!enrol.isInFinalStatus()) {
                        enrol.setStatus(EnrolmentStatus.IN_TRANSACTION);
                    }
                }
            }
        }
        return this;
    }

    public static PaymentInFail valueOf(PaymentInModel model)
    {
        PaymentInFail paymentInSucceed = new PaymentInFail();
        paymentInSucceed.model = model;
        return paymentInSucceed;
    }
}
