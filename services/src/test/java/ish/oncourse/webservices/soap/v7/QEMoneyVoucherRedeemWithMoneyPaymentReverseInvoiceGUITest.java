package ish.oncourse.webservices.soap.v7;

import ish.common.types.*;
import ish.oncourse.webservices.util.GenericEnrolmentStub;
import ish.oncourse.webservices.util.GenericPaymentInStub;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.v7.stubs.replication.VoucherStub;
import org.apache.cayenne.ObjectContext;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class QEMoneyVoucherRedeemWithMoneyPaymentReverseInvoiceGUITest extends QEMoneyVoucherRedeemWithMoneyPaymentTest {
	private static final String DEFAULT_DATASET_XML = "ish/oncourse/webservices/soap/QEMoneyVoucherRedeemWithMoneyPaymentReverseInvoiceDataSet.xml";

	@Override
	protected String getDataSetFile() {
		return DEFAULT_DATASET_XML;
	}

	@Override
	protected GenericTransactionGroup prepareStubsForReplication(GenericTransactionGroup transaction) {
		return preparePaymentStructureForMoneyVoucherAndMoneyPayment(transaction);
	}

	@Override
	protected void checkProcessedResponse(GenericTransactionGroup transaction) {
		assertFalse("Get status call should not return empty response for payment in final status",
			transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		assertEquals("25 elements should be replicated for this payment", 25, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		int reversePaymentCount = 0;
		//parse the transaction results
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof GenericPaymentInStub) {
				GenericPaymentInStub paymentInStub = (GenericPaymentInStub) stub;
				if (stub.getAngelId() != null) {
					if (stub.getAngelId() == 1l) {
						assertEquals("This should be money credit card payment", PaymentType.CREDIT_CARD.getDatabaseValue(), paymentInStub.getType());
						PaymentStatus status = TypesUtil.getEnumForDatabaseValue(paymentInStub.getStatus(), PaymentStatus.class);
						assertEquals("Payment status should be success", PaymentStatus.FAILED_CARD_DECLINED, status);
					} else if (stub.getAngelId() == 2l) {
						assertEquals("This should be voucher payment", PaymentType.VOUCHER.getDatabaseValue(), paymentInStub.getType());
						PaymentStatus status = TypesUtil.getEnumForDatabaseValue(paymentInStub.getStatus(), PaymentStatus.class);
						assertEquals("Payment status should be success", PaymentStatus.FAILED, status);
					} else {
						assertFalse(String.format("Unexpected PaymentIn with id= %s angelid=%s and status= %s found in a queue",
							stub.getWillowId(), stub.getAngelId(), paymentInStub.getStatus()), true);
					}
				} else {
					if (reversePaymentCount == 0) {
						assertEquals("This should be reverse payment", PaymentType.REVERSE.getDatabaseValue(), paymentInStub.getType());
						PaymentStatus status = TypesUtil.getEnumForDatabaseValue(paymentInStub.getStatus(), PaymentStatus.class);
						assertEquals("Payment status should be success", PaymentStatus.SUCCESS, status);
						reversePaymentCount++;
					} else {
						assertFalse(String.format("Unexpected PaymentIn with id= %s angelid=%s and status= %s found in a queue",
								stub.getWillowId(), stub.getAngelId(), paymentInStub.getStatus()), true);
					}
				}
			} else if (stub instanceof GenericEnrolmentStub) {
				if (stub.getAngelId() == 1l || stub.getAngelId() == 2l) {
					EnrolmentStatus status = EnrolmentStatus.valueOf(((GenericEnrolmentStub) stub).getStatus());
					assertEquals("Oncourse enrollment should be failed", EnrolmentStatus.FAILED, status);
				} else {
					assertFalse(String.format("Unexpected Enrolment with id= %s and status= %s found in a queue", stub.getWillowId(),
							((GenericEnrolmentStub)stub).getStatus()), true);
				}
			} else if (stub instanceof VoucherStub) {
				assertEquals("Check of voucher status failed", ((VoucherStub) stub).getStatus(), ProductStatus.ACTIVE.getDatabaseValue());
				assertEquals("Value on purchase should not change", new BigDecimal("200.00"), ((VoucherStub) stub).getValueOnPurchase());
			}
		}
	}

	@Override
	protected String checkResponseAndReceiveSessionId(GenericTransactionGroup transaction) {
		return checkResponseAndReceiveSessionIdForVoucherAndCreditCardPayment(transaction);
	}

	@Test
	public void testQEPayment() throws Exception {
		testFailedGUICases();
	}

	@Override
	protected void checkAsyncReplication(ObjectContext context) {
		checkAsyncReplicationForVoucherAndCreditCardReverseInvoicePayment(context);
	}

}
