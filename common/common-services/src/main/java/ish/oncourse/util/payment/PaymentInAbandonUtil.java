package ish.oncourse.util.payment;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.ProductItem;

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
	
	static boolean isHaveEnrollmentsForKeepInvoice(final PaymentIn payment) {
		Expression paymentIdMatchExpression = ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, payment.getId());
		Expression enrollmentExpression = ExpressionFactory.matchExp(PaymentIn.PAYMENT_IN_LINES_PROPERTY + "." + PaymentInLine.INVOICE_PROPERTY + "." + 
			Invoice.INVOICE_LINES_PROPERTY + "." + InvoiceLine.ENROLMENT_PROPERTY + "." + Enrolment.STATUS_PROPERTY, EnrolmentStatus.SUCCESS);
		SelectQuery checkQuery = new SelectQuery(PaymentIn.class, paymentIdMatchExpression.andExp(enrollmentExpression));
		@SuppressWarnings("unchecked")
		List<PaymentIn> successEnrollmentsResult = payment.getObjectContext().performQuery(checkQuery);
		return !successEnrollmentsResult.isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	static boolean isHaveInvoicesNotLinkedToPayableEntities(final PaymentIn payment) {
		Expression invoicesWithoutEnrollmentRelation = ExpressionFactory.matchExp(PaymentIn.PAYMENT_IN_LINES_PROPERTY + "." + 
			PaymentInLine.INVOICE_PROPERTY + "." + Invoice.INVOICE_LINES_PROPERTY + "." + InvoiceLine.ENROLMENT_PROPERTY, null);
		Expression invoicesWithoutProductRelation = ExpressionFactory.matchDbExp(ProductItem.INVOICE_LINE_PROPERTY + "." + InvoiceLine.INVOICE_PROPERTY 
				+ "." + Invoice.PAYMENT_IN_LINES_PROPERTY + "." + PaymentInLine.PAYMENT_IN_PROPERTY + "." + PaymentIn.ID_PK_COLUMN, payment.getId());
		Expression notZeroOwingInvoices = ExpressionFactory.noMatchExp(PaymentIn.PAYMENT_IN_LINES_PROPERTY + "." + 
			PaymentInLine.INVOICE_PROPERTY + "." + Invoice.AMOUNT_OWING_PROPERTY, Money.ZERO.toBigDecimal());
		Expression paymentIdMatchExpression = ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, payment.getId());
		SelectQuery checkEnrollmentsQuery = new SelectQuery(PaymentIn.class, paymentIdMatchExpression.andExp(notZeroOwingInvoices)
			.andExp(invoicesWithoutEnrollmentRelation));
		SelectQuery checkProductsQuery = new SelectQuery(ProductItem.class, invoicesWithoutProductRelation);
		
		List<PaymentIn> enrollmentsResult = payment.getObjectContext().performQuery(checkEnrollmentsQuery);
		List<ProductItem> productsResult =  payment.getObjectContext().performQuery(checkProductsQuery);
		return !enrollmentsResult.isEmpty() && productsResult.isEmpty();
	}
	
	public static void abandonPaymentReverseInvoice(final PaymentIn payment, final boolean createReverseInvoice) {
		try {
			logger.info(String.format("Canceling paymentIn with id:%s, created:%s and status:%s.", payment.getId(), payment.getCreated(), payment.getStatus()));
			if (!createReverseInvoice || isHaveEnrollmentsForKeepInvoice(payment)) {
				//we should not reverse enrollments when college allow them to enroll with owing.
				payment.abandonPaymentKeepInvoice();
			} else {
				payment.abandonPayment();
			}
			//we should set message that this payment was expired by timeout
			if (!createReverseInvoice) {
				payment.setStatusNotes(PaymentStatus.PAYMENT_EXPIRED_BY_TIMEOUT_MESSAGE);
			}
			payment.getObjectContext().commitChanges();
		} catch (final CayenneRuntimeException ce) {
			logger.debug(String.format("Unable to cancel payment with id:%s and status:%s.", payment.getId(), payment.getStatus()), ce);
			payment.getObjectContext().rollbackChanges();
		}
	}
}
