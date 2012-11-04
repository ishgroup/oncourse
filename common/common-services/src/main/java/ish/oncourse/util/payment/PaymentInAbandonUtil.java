package ish.oncourse.util.payment;

import ish.common.types.EnrolmentStatus;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility which contain logic for correct default abandon action.
 * @author vdavidovich
 *
 */
public class PaymentInAbandonUtil {
	private static final Logger logger = Logger.getLogger(PaymentInAbandonUtil.class);
	
	public static void abandonPaymentReverseInvoice(final PaymentIn payment) {
		try {
			logger.info(String.format("Canceling paymentIn with id:%s, created:%s and status:%s.", payment.getId(), payment.getCreated(),
					payment.getStatus()));
			// we should check that there is no enrollments with amount owing exist in this payment.
			final List<Enrolment> enrollmentsForKeepInvoice = new ArrayList<Enrolment>();
			if (payment.getPaymentInLines() != null) {
				for (final PaymentInLine paymentLine: payment.getPaymentInLines()) {
					if (paymentLine.getInvoice().getInvoiceLines() != null) {
						for (final InvoiceLine invoiceLine : paymentLine.getInvoice().getInvoiceLines()) {
							if (invoiceLine.getEnrolment() != null && EnrolmentStatus.SUCCESS.equals(invoiceLine.getEnrolment().getStatus())) {
								enrollmentsForKeepInvoice.add(invoiceLine.getEnrolment());
							}
						}
					}
				}
			}
			if (enrollmentsForKeepInvoice.isEmpty()) {
				//if all enrollments in transaction we can just fail them
				payment.abandonPayment();
			} else {
				//we should not fail enrollments when college allow them to enroll with owing.
				payment.abandonPaymentKeepInvoice();
			}
			payment.getObjectContext().commitChanges();
		} catch (final CayenneRuntimeException ce) {
			logger.debug(String.format("Unable to cancel payment with id:%s and status:%s.", payment.getId(), payment.getStatus()), ce);
			payment.getObjectContext().rollbackChanges();
		}
	}
}
