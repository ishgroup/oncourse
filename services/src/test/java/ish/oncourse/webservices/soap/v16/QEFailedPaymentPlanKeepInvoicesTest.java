/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.soap.v16;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.common.types.TypesUtil;
import ish.oncourse.webservices.util.*;
import org.apache.cayenne.ObjectContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class QEFailedPaymentPlanKeepInvoicesTest extends QEPaymentPlanGUITest {


	@Override
	protected void checkProcessedResponse(GenericTransactionGroup transaction) {
		assertFalse("Get status call should not return empty response for payment in final status",
				transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		assertEquals("16 elements should be replicated for this payment", 19, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		//parse the transaction results
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof GenericPaymentInStub) {
				PaymentStatus status = TypesUtil.getEnumForDatabaseValue(((GenericPaymentInStub) stub).getStatus(), PaymentStatus.class);
				if (stub.getAngelId() == null) {
					assertEquals("Payment status should be failed after expiration", PaymentStatus.FAILED, status);
				} else if (stub.getAngelId() == 1l) {
					assertEquals("Payment status should be failed after expiration", PaymentStatus.FAILED_CARD_DECLINED, status);
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

	@Test
	public void testPaymentPlanKeepInvoices() throws Exception {
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
		renderPaymentPageWithKeepInvoiceProcessing(sessionId);

		//check that async replication works correct
		checkAsyncReplication(context);

		//check the status via service when processing complete
		checkProcessedResponse(getPaymentStatus(sessionId));
	}
}
