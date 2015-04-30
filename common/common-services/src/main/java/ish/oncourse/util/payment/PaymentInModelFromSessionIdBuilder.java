package ish.oncourse.util.payment;

import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class PaymentInModelFromSessionIdBuilder {

    private ObjectContext context;
    private String sessionId;

    private PaymentInModel model;

    private PaymentInModelFromSessionIdBuilder build() {

        PaymentInModel model = new PaymentInModel();

        PaymentIn paymentIn = ObjectSelect.query(PaymentIn.class).where(PaymentIn.SESSION_ID.eq(sessionId)).selectOne(context);
        model.setPaymentIn(paymentIn);
        List<Invoice> invoices = ObjectSelect.query(Invoice.class).where(Invoice.SESSION_ID.eq(sessionId)).select(context);
        for (Invoice invoice : invoices) {
            model.getInvoices().add(invoice);
            for (InvoiceLine invoiceLine : invoice.getInvoiceLines()) {
                model.getEnrolments().add(invoiceLine.getEnrolment());
            }
        }
        return this;
    }


    public PaymentInModel getModel() {
        return model;
    }

    public static PaymentInModelFromSessionIdBuilder valueOf(String sessionId, ObjectContext context) {
        PaymentInModelFromSessionIdBuilder builder = new PaymentInModelFromSessionIdBuilder();
        builder.sessionId = sessionId;
        builder.context = context;
        return builder;
    }

}
