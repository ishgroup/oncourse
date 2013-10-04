package ish.oncourse.util.payment;

import ish.common.types.EnrolmentStatus;
import ish.oncourse.model.*;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Utility which contain logic for correct default abandon action.
 * @author vdavidovich
 *
 */
public class PaymentInAbandonUtil {
	private static final Logger logger = Logger.getLogger(PaymentInAbandonUtil.class);


	static boolean hasSuccessEnrolments(PaymentIn payment) {
		Expression paymentIdMatchExpression = ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, payment.getId());
		Expression enrollmentExpression = ExpressionFactory.matchExp(PaymentIn.PAYMENT_IN_LINES_PROPERTY + "." +
				PaymentInLine.INVOICE_PROPERTY + "." +
				Invoice.INVOICE_LINES_PROPERTY + "." +
				InvoiceLine.ENROLMENT_PROPERTY + "." +
				Enrolment.STATUS_PROPERTY, EnrolmentStatus.SUCCESS);
		SelectQuery checkQuery = new SelectQuery(PaymentIn.class, paymentIdMatchExpression.andExp(enrollmentExpression));
		@SuppressWarnings("unchecked")
		List<PaymentIn> successEnrollmentsResult = payment.getObjectContext().performQuery(checkQuery);
		return !successEnrollmentsResult.isEmpty();
	}
	
	public static void abandonPayment(PaymentIn payment, boolean reverseInvoice) {
		try {
			logger.info(String.format("Canceling paymentIn with id:%s, created:%s and status:%s.", payment.getId(), payment.getCreated(), payment.getStatus()));

			if (reverseInvoice && !hasSuccessEnrolments(payment))
				payment.abandonPayment();
			else
				payment.abandonPaymentKeepInvoice();
			payment.getObjectContext().commitChanges();
		} catch (final CayenneRuntimeException ce) {
			logger.debug(String.format("Unable to cancel payment with id:%s and status:%s.", payment.getId(), payment.getStatus()), ce);
			payment.getObjectContext().rollbackChanges();
		}
	}
}
