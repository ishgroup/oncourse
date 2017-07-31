package ish.oncourse.webservices.soap.v16;

import ish.oncourse.webservices.util.GenericParametersMap;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import org.apache.cayenne.ObjectContext;
import org.junit.Test;

public class QECourseVoucherRedeemWithMoneyPaymentSuccessGUITest extends QECourseVoucherRedeemWithMoneyPaymentTest {
	private static final String DEFAULT_DATASET_XML = "ish/oncourse/webservices/soap/QECourseVoucherRedeemWithMoneyPaymentSuccessDataSet.xml";

	@Override
	protected String getDataSetFile() {
		return DEFAULT_DATASET_XML;
	}

	@Override
	protected void prepareStubsForReplication(GenericTransactionGroup transaction, GenericParametersMap parametersMap) {
		preparePaymentStructureForCourseVoucherAndMoneyPayment(transaction,parametersMap);
	}

	@Override
	protected void checkProcessedResponse(GenericTransactionGroup transaction) {
		checkProcessedResponseForVoucherAndCreditCardPayment(transaction);
	}

	@Override
	protected void checkAsyncReplication(ObjectContext context) {
		checkAsyncReplicationForVoucherAndCreditCardPayment(context);
	}

	@Override
	protected String checkResponseAndReceiveSessionId(GenericTransactionGroup transaction) {
		return checkResponseAndReceiveSessionIdForVoucherAndCreditCardPayment(transaction);
	}

	@Test
	public void testQESuccessPayment() throws Exception {
		testSuccessGUICases();
	}
}
