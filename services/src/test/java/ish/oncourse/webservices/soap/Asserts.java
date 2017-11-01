/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.soap;

import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.common.types.ProductStatus;
import ish.common.types.TypesUtil;
import ish.oncourse.webservices.util.GenericEnrolmentStub;
import ish.oncourse.webservices.util.GenericPaymentInStub;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.v10.stubs.replication.ArticleStub;
import ish.oncourse.webservices.v10.stubs.replication.MembershipStub;
import ish.oncourse.webservices.v10.stubs.replication.VoucherStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * User: akoiro
 * Date: 2/11/17
 */
public class Asserts {
	public static void assertQEPreviouslyKeepNoActiveItemsWithNewInvoiceTest(GenericTransactionGroup transaction) {
		assertFalse("Get status call should not return empty response for payment in final status",
				transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		assertEquals("21 elements should be replicated for this payment", 21, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		//parse the transaction results

		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {

			if (stub instanceof GenericPaymentInStub) {
				PaymentStatus status = TypesUtil.getEnumForDatabaseValue(((GenericPaymentInStub) stub).getStatus(), PaymentStatus.class);
				if (stub.getWillowId() == 11l) {
					assertEquals("Payment status should be failed after expiration", PaymentStatus.FAILED_CARD_DECLINED, status);
				} else if (stub.getWillowId() == 12l) {
					assertEquals("Payment status should be failed after expiration", PaymentStatus.FAILED, status);
					PaymentType type = TypesUtil.getEnumForDatabaseValue(((GenericPaymentInStub) stub).getType(), PaymentType.class);
					assertEquals("Payment type should be CC", PaymentType.CREDIT_CARD, type);
				} else {
					assertFalse(String.format("Unexpected PaymentIn with id= %s and status= %s found in a queue", stub.getWillowId(),
							((GenericPaymentInStub) stub).getStatus()), true);
				}
			} else if (stub instanceof GenericEnrolmentStub) {
				assertFalse(String.format("Unexpected Enrolment with id= %s and status= %s found in a queue", stub.getWillowId(),
						((GenericEnrolmentStub)stub).getStatus()), true);
			} else if (stub instanceof VoucherStub) {
				if (stub.getAngelId().equals(2l)) {
					assertEquals("Voucher status should be active",
							ProductStatus.ACTIVE.getDatabaseValue(), ((VoucherStub) stub).getStatus());
				} else {
					assertFalse(String.format("Unexpected Voucher with id= %s and status= %s found in a queue", stub.getWillowId(),
							((VoucherStub) stub).getStatus()), true);
				}
			} else if (stub instanceof ArticleStub) {
				if (stub.getAngelId().equals(3l)) {
					assertEquals("Article status should be active",
							ProductStatus.ACTIVE.getDatabaseValue(), ((ArticleStub) stub).getStatus());
				} else {
					assertFalse(String.format("Unexpected Article with id= %s and status= %s found in a queue", stub.getWillowId(),
							((ArticleStub) stub).getStatus()), true);
				}
			} else if (stub instanceof MembershipStub) {
				if (stub.getAngelId().equals(1l)) {
					assertEquals("Membership status should be active",
							ProductStatus.ACTIVE.getDatabaseValue(), ((MembershipStub) stub).getStatus());
				} else {
					assertFalse(String.format("Unexpected Membership with id= %s and status= %s found in a queue", stub.getWillowId(),
							((MembershipStub) stub).getStatus()), true);
				}
			}
		}
	}

}
