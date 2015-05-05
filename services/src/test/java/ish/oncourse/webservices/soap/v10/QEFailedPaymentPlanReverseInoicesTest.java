/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.soap.v10;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.math.Money;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.util.GenericEnrolmentStub;
import ish.oncourse.webservices.util.GenericInvoiceStub;
import ish.oncourse.webservices.util.GenericParametersMap;
import ish.oncourse.webservices.util.GenericPaymentInStub;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.v10.stubs.replication.EnrolmentStub;
import ish.oncourse.webservices.v10.stubs.replication.InvoiceStub;
import ish.oncourse.webservices.v10.stubs.replication.PaymentInStub;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class QEFailedPaymentPlanReverseInoicesTest extends QEPaymentPlanGUITest {


	@Override
	protected void checkAsyncReplication(ObjectContext context) {
		@SuppressWarnings("unchecked")
		List<QueuedRecord> queuedRecords = context.performQuery(new SelectQuery(QueuedRecord.class));
		assertFalse("Queue should not be empty after page processing", queuedRecords.isEmpty());
		assertEquals("Queue should contain 21 records.", 21, queuedRecords.size());
		int paymentsFound = 0, paymentLinesFound = 0, invoicesFound = 0, invoiceLinesFound = 0,
				enrolmentsFound = 0;
		for (QueuedRecord record : queuedRecords) {
			if (PAYMENT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				paymentsFound++;
			} else if (PAYMENT_LINE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				paymentLinesFound++;
			} else if (INVOICE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				invoicesFound++;
			} else if (INVOICE_LINE_IDENTIFIER.equals(record.getEntityIdentifier())) {
				invoiceLinesFound++;
			} else if (ENROLMENT_IDENTIFIER.equals(record.getEntityIdentifier())) {
				enrolmentsFound++;
			}
		}
		assertEquals("Not all PaymentIns found in a queue", 3, paymentsFound);
		assertEquals("Not all PaymentInLines found in a queue", 6, paymentLinesFound);
		assertEquals("Not all Invoices found in a queue", 4, invoicesFound);
		assertEquals("Not all InvoiceLines found in a  queue", 4, invoiceLinesFound);
		assertEquals("Not all Enrolments found in a  queue", 2, enrolmentsFound);
	}

	@Override
	protected void checkProcessedResponse(GenericTransactionGroup transaction) {
		assertFalse("Get status call should not return empty response for payment in final status",
				transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		assertEquals("26 elements should be replicated for this payment", 26, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		//parse the transaction results
		
		int ccPaymentCount = 0;
		int	reversPaymentCount = 0;
		int	enrolmentsCount = 0;
		int	invoicesCount = 0;
		
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof GenericPaymentInStub) {

				PaymentInStub paymentStub = (PaymentInStub) stub;
				
				if (PaymentType.CREDIT_CARD.getDatabaseValue().equals(paymentStub.getType())) {
					assertEquals("Payment status should be failed", PaymentStatus.FAILED_CARD_DECLINED.getDatabaseValue(), paymentStub.getStatus());
					ccPaymentCount++;
					
				} else if (PaymentType.REVERSE.getDatabaseValue().equals(paymentStub.getType())) {
					assertEquals("Revers payment should be success",PaymentStatus.SUCCESS.getDatabaseValue(), paymentStub.getStatus());
					reversPaymentCount++;
				} else {
					assertFalse(String.format("Unexpected PaymentIn with id= %s and status= %s found in a queue", stub.getWillowId(),
							((GenericPaymentInStub) stub).getStatus()), true);
				}
				
			} else if (stub instanceof GenericEnrolmentStub) {
				EnrolmentStub enrolmentStub = (EnrolmentStub) stub;
				assertEquals("Enrolments should be failed", EnrolmentStatus.FAILED, EnrolmentStatus.valueOf(enrolmentStub.getStatus()));
				enrolmentsCount++;
			} else if (stub instanceof GenericInvoiceStub) {
				InvoiceStub invoiceStub = (InvoiceStub) stub;
				assertEquals("All Invoices should be repaid", invoiceStub.getAmountOwing(), Money.ZERO.toBigDecimal());
				invoicesCount++;
			}
		}
		assertEquals("Not all PaymentIns found", 1, ccPaymentCount);
		assertEquals("Not all ReverseInvoices found", 2, reversPaymentCount);
		assertEquals("Not all Enrolments found ", 2, enrolmentsCount);
		assertEquals("Not all Invoices found ", 4, invoicesCount);
	}

	@Test
	public void testPaymentPlanReversInvoices() throws Exception {
		//check that empty queuedRecords
		ObjectContext context = cayenneService.newNonReplicatingContext();
		checkQueueBeforeProcessing(context);
		authenticate();
		// prepare the stubs for replication
		GenericTransactionGroup transaction = PortHelper.createTransactionGroup(getSupportedVersion());
		GenericParametersMap parametersMap = PortHelper.createParametersMap(getSupportedVersion());

		fillv10PaymentStubs(transaction, parametersMap);
		//process payment
		transaction = getPaymentPortType().processPayment(castGenericTransactionGroup(transaction), castGenericParametersMap(parametersMap));

		//check the response, validate the data and receive the sessionid
		String sessionId = checkResponseAndReceiveSessionId(transaction);
		checkQueueAfterProcessing(context);

		//check the status via service
		checkNotProcessedResponse(getPaymentStatus(sessionId));

		//call page processing
		testRenderPaymentPageWithReverseInvoice(sessionId);

		//check that async replication works correct
		checkAsyncReplication(context);

		//check the status via service when processing complete
		checkProcessedResponse(getPaymentStatus(sessionId));
	}
}
