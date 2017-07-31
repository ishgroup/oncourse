package ish.oncourse.webservices.soap.v16;

import ish.common.types.*;
import ish.oncourse.webservices.util.*;
import ish.oncourse.webservices.v16.stubs.replication.VoucherStub;
import org.apache.cayenne.ObjectContext;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class QEMoneyVoucherRedeemFailedNoPlacesNoGUITest extends QEVoucherRedeemFailedNoGUITest {
	private static final String DEFAULT_DATASET_XML = "ish/oncourse/webservices/soap/QEMoneyVoucherRedeemFailedNoPlacesDataSet.xml";

	@Override
	protected String getDataSetFile() {
		return DEFAULT_DATASET_XML;
	}

	@Override
	protected void prepareStubsForReplication(GenericTransactionGroup transaction, GenericParametersMap parametersMap) {
		prepareMoneyVoucherNoMoneyPayment(transaction, parametersMap);
	}

	@Override
	protected void checkProcessedResponse(GenericTransactionGroup transaction) {
		assertFalse("Get status call should not return empty response for payment in final status",
			transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty());
		assertEquals("23 elements should be replicated for this payment", 23, transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());
		//parse the transaction results
		for (GenericReplicationStub stub : transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			if (stub instanceof GenericPaymentInStub) {
				GenericPaymentInStub paymentInStub = (GenericPaymentInStub) stub;
				if (stub.getAngelId() == null) {
					if (stub.getWillowId() == 3l || stub.getWillowId() == 4l) {
						assertEquals("This should be 0 amount internal payment", PaymentType.REVERSE.getDatabaseValue(), paymentInStub.getType());
						PaymentStatus status = TypesUtil.getEnumForDatabaseValue(paymentInStub.getStatus(), PaymentStatus.class);
						assertEquals("Payment status should be success", PaymentStatus.SUCCESS, status);
					} else {
						assertFalse(String.format("Unexpected PaymentIn with id= %s angelid=%s and status= %s found in a queue",
							stub.getWillowId(), stub.getAngelId(), paymentInStub.getStatus()), true);
					}
				} else if (stub.getAngelId() == 1l) {
					assertEquals("This should be 0 amount internal payment", PaymentType.INTERNAL.getDatabaseValue(), paymentInStub.getType());
					PaymentStatus status = TypesUtil.getEnumForDatabaseValue(paymentInStub.getStatus(), PaymentStatus.class);
					assertEquals("Payment status should be failed no places", PaymentStatus.FAILED_NO_PLACES, status);
				} else if (stub.getAngelId() == 2l) {
					assertEquals("This should be voucher payment", PaymentType.VOUCHER.getDatabaseValue(), paymentInStub.getType());
					PaymentStatus status = TypesUtil.getEnumForDatabaseValue(paymentInStub.getStatus(), PaymentStatus.class);
					assertEquals("Payment status should be failed after expiration", PaymentStatus.FAILED, status);
				} else {
					assertFalse(String.format("Unexpected PaymentIn with id= %s angelid=%s and status= %s found in a queue",
							stub.getWillowId(), stub.getAngelId(), paymentInStub.getStatus()), true);
				}
			} else if (stub instanceof GenericEnrolmentStub) {
				if (stub.getAngelId() == 1l) {
					EnrolmentStatus status = EnrolmentStatus.valueOf(((GenericEnrolmentStub) stub).getStatus());
					assertEquals("Oncourse enrollment should be success after expiration", EnrolmentStatus.FAILED, status);
				} else {
					assertFalse(String.format("Unexpected Enrolment with id= %s and status= %s found in a queue", stub.getWillowId(),
						((GenericEnrolmentStub)stub).getStatus()), true);
				}
			} else if (stub instanceof GenericInvoiceStub) {
				GenericInvoiceStub invoiceStub = (GenericInvoiceStub) stub;
				if (stub.getAngelId() == null && stub.getWillowId() == 2l) {
					assertEquals("Incorrect reverse invoice amount", getInvoiceTotalGst(invoiceStub), new BigDecimal("-100.00"));
				} else if (stub.getAngelId() == 10l) {
					assertEquals("Incorrect original invoice amount", getInvoiceTotalGst(invoiceStub), new BigDecimal("100.00"));
				} else if (stub.getAngelId() == 4l) {
					assertEquals("Incorrect original invoice amount", getInvoiceTotalGst(invoiceStub), new BigDecimal("10.91"));
				} else {
					assertFalse(String.format("Unexpected Invoice with id= %s and angelid= %s found in a queue",
						stub.getWillowId(), stub.getAngelId()), true);
				}
			} else if (isVoucherPaymentInStub(stub)) {
				assertEquals("Incorrect VoucherPaymentIn status", getVoucherPaymentInStatus(stub), VoucherPaymentStatus.APPROVED.getDatabaseValue());
			} else if (stub instanceof VoucherStub) {
				assertEquals("Check of voucher redemption value failed", ((VoucherStub) stub).getRedemptionValue(), new BigDecimal("200.00"));
				assertEquals("Check of voucher status failed", ((VoucherStub) stub).getStatus(), ProductStatus.ACTIVE.getDatabaseValue());
				assertEquals("Value on purchase shouldn't change", new BigDecimal("200.00"), ((VoucherStub) stub).getValueOnPurchase());
			}
		}
	}

	@Override
	protected void checkAsyncReplication(ObjectContext context) {
		checkAsyncReplicationForVoucherNoGUIFailed(context);
	}

	@Test
	public void testQEFailedPayment() throws Exception {
		testNoGUICases();
	}
}
