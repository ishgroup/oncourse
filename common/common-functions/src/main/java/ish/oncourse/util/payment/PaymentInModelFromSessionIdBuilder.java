package ish.oncourse.util.payment;

import ish.common.types.PaymentStatus;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.utils.PaymentInUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static java.lang.String.format;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class PaymentInModelFromSessionIdBuilder {
	private static final Logger logger = LogManager.getLogger();

    private ObjectContext context;
    private String sessionId;

    private PaymentInModel model = new PaymentInModel();

    public PaymentInModelFromSessionIdBuilder build() {

		try {
			PaymentIn paymentIn = ObjectSelect.query(PaymentIn.class).where(PaymentIn.SESSION_ID.eq(sessionId)).and(PaymentIn.STATUS.in(PaymentStatus.IN_TRANSACTION, PaymentStatus.NEW, PaymentStatus.CARD_DETAILS_REQUIRED)).prefetch(PaymentIn.PAYMENT_TRANSACTIONS.joint()).selectOne(context);
			model.setPaymentIn(paymentIn);
			List<Invoice> invoices = ObjectSelect.query(Invoice.class).where(Invoice.SESSION_ID.eq(sessionId)).select(context);
			for (Invoice invoice : invoices) {
				model.getInvoices().add(invoice);
				for (InvoiceLine invoiceLine : invoice.getInvoiceLines()) {
					model.getEnrolments().add(invoiceLine.getEnrolment());
				}
			}

			if (model.getPaymentIn() != null) {
				initVoucherPayments();
			}
		} catch (Exception e) {
			String message = format("Cannot create PaymentInModel for sessionId: %s", sessionId);
			logger.error(message, e);
			throw new IllegalStateException(message, e);
		}
		return this;
    }

	private void initVoucherPayments() {
		for (PaymentIn voucherPayment : PaymentInUtil.getRelatedVoucherPayments(model.getPaymentIn())) {
			model.getVoucherPayments().add(voucherPayment);
		}
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
