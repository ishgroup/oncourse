/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.util.payment;

import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.payment.IPaymentService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.Calendar;
import java.util.List;

import static ish.oncourse.util.payment.CompleteInTransactionPayments.CompleteResult.*;

public class CompleteInTransactionPayments {

	private ObjectContext context;
	private PaymentIn current;
	private Contact contact;
	private IPaymentService paymentService;
		
	private CompleteInTransactionPayments() {}

	public static CompleteInTransactionPayments valueOf(ObjectContext context, PaymentIn current, Contact contact, IPaymentService paymentService) {
		CompleteInTransactionPayments completer = new CompleteInTransactionPayments();
		completer.context = context;
		completer.current = current;
		completer.contact = contact;
		completer.paymentService = paymentService;
		
		return completer;
	}
	
	public CompleteResult complite() {
		CompleteResult status = NOTHING_TO_COMPLETE;
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -PaymentIn.EXPIRE_TIME_WINDOW);

		List<PaymentIn> payments = ObjectSelect.query(PaymentIn.class).
				where(PaymentIn.STATUS.in(PaymentStatus.IN_TRANSACTION, PaymentStatus.CARD_DETAILS_REQUIRED)).
				and(PaymentIn.CONTACT.eq(contact)).
				and(PaymentIn.SOURCE.eq(PaymentSource.SOURCE_WEB)).
				and(PaymentIn.CREATED.gt(calendar.getTime())).
				select(context);

		for (PaymentIn p : payments) {
			if (!current.getObjectId().isTemporary() &&
					current.getId().equals(p.getId()))
				continue;

			if (!paymentService.isProcessedByGateway(p)){
				context.rollbackChanges();
				return NOT_COMPLETE;
			}

			status = COMPLETE;

			PaymentInModel model = PaymentInModelFromPaymentInBuilder.valueOf(p).build().getModel();
			PaymentInAbandon.valueOf(model, false).perform();
		}

		context.commitChanges();
		return status;
	}
	
	public enum CompleteResult {
		NOTHING_TO_COMPLETE,
		COMPLETE,
		NOT_COMPLETE
	}
}
