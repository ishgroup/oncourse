package ish.oncourse.util.payment;

import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.utils.PaymentInUtil;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class PaymentInModelFromPaymentInBuilder {
    private PaymentIn paymentIn;

    private PaymentInModel model = new PaymentInModel();

    public PaymentInModelFromPaymentInBuilder build() {
        model.setPaymentIn(paymentIn);
        for (PaymentInLine line: paymentIn.getPaymentInLines()) {
            model.getInvoices().add(line.getInvoice());
            for (InvoiceLine invoiceLine: line.getInvoice().getInvoiceLines())
            {
                if (invoiceLine.getEnrolment() != null) {
                    model.getEnrolments().add(invoiceLine.getEnrolment());
                }
            }
        }
        initVoucherPayments();

        return this;
    }

    private void initVoucherPayments() {
        for (PaymentIn voucherPayment :	PaymentInUtil.getRelatedVoucherPayments(model.getPaymentIn())) {
            model.getVoucherPayments().add(voucherPayment);
        }
    }

    public static PaymentInModelFromPaymentInBuilder valueOf(PaymentIn paymentIn) {
        PaymentInModelFromPaymentInBuilder builder = new PaymentInModelFromPaymentInBuilder();
        builder.paymentIn = paymentIn;
        return builder;
    }

    public PaymentInModel getModel() {
        return model;
    }
}
