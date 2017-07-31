/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.soap.v16;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.common.types.TypesUtil;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.util.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class QESuccessPaymentPlanTest extends QEPaymentPlanGUITest {


	@Override
	protected void checkProcessedResponse(GenericTransactionGroup transaction) {
		assertFalse("Get status call should not return empty response for payment in final status",
				transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		assertEquals("16 elements should be replicated for this payment", 16, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		//parse the transaction results
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof GenericPaymentInStub) {
				if (stub.getAngelId() == 1l) {
					PaymentStatus status = TypesUtil.getEnumForDatabaseValue(((GenericPaymentInStub) stub).getStatus(), PaymentStatus.class);
					assertEquals("Payment status should be failed after expiration", PaymentStatus.SUCCESS, status);
				} else {
					assertFalse(String.format("Unexpected PaymentIn with id= %s and status= %s found in a queue", stub.getWillowId(),
							((GenericPaymentInStub) stub).getStatus()), true);
				}
			} else if (stub instanceof GenericEnrolmentStub) {
				if (stub.getAngelId() == 1l || stub.getAngelId() == 2l) {
					EnrolmentStatus status = EnrolmentStatus.valueOf(((GenericEnrolmentStub) stub).getStatus());
					assertEquals("Oncourse enrollment should be success after processing", EnrolmentStatus.SUCCESS, status);
				} else {
					assertFalse(String.format("Unexpected Enrolment with id= %s and status= %s found in a queue", stub.getWillowId(),
							((GenericEnrolmentStub)stub).getStatus()), true);
				}
			} 
		}
	}

	@Override
	protected void  checkAsyncReplication(ObjectContext context) {
		List<QueuedRecord> queuedRecords = ObjectSelect.query(QueuedRecord.class)
				.select(context);
		assertFalse("Queue should not be empty after page processing", queuedRecords.isEmpty());
		assertEquals("Queue should contain 9 records.", 9, queuedRecords.size());
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
			} else {
				assertFalse("Unexpected queued record found in a queue after QE processing for entity " + record.getEntityIdentifier(), true);
			}
		}
		assertEquals("Not all PaymentIns found in a queue", 1, paymentsFound);
		assertEquals("Not all PaymentInLines found in a queue", 2, paymentLinesFound);
		assertEquals("Not all Invoices found in a queue", 2, invoicesFound);
		assertEquals("Not all InvoiceLines found in a  queue", 2, invoiceLinesFound);
		assertEquals("Not all Enrolments found in a  queue", 2, enrolmentsFound);
	}

	@Test
	public void testSuccessPaymentPlanQE() throws Exception {
		//check that empty queuedRecords
		ObjectContext context = cayenneService.newNonReplicatingContext();
		checkQueueBeforeProcessing(context);
		authenticate();
		// prepare the stubs for replication
		GenericTransactionGroup transaction = PortHelper.createTransactionGroup(getSupportedVersion());
		GenericParametersMap parametersMap = PortHelper.createParametersMap(getSupportedVersion());
		
		fillv16PaymentStubs(transaction, parametersMap);
		//process payment
		transaction = getPaymentPortType().processPayment(castGenericTransactionGroup(transaction), castGenericParametersMap(parametersMap));
		
		//check the response, validate the data and receive the sessionid
		String sessionId = checkResponseAndReceiveSessionId(transaction);
		checkQueueAfterProcessing(context);
		
		//check the status via service
		checkNotProcessedResponse(getPaymentStatus(sessionId));
		
		//call page processing
		renderPaymentPageWithSuccessProcessing(sessionId);
		
		//check that async replication works correct
		checkAsyncReplication(context);
		
		//check the status via service when processing complete
		checkProcessedResponse(getPaymentStatus(sessionId));
	}
}
