package ish.oncourse.webservices.soap.v16;

import ish.oncourse.webservices.util.GenericParametersMap;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

public class QEMoneyVoucherRedeemWithMoneyPaymentSuccessGUITest extends QEMoneyVoucherRedeemWithMoneyPaymentTest {
	private static final String DEFAULT_DATASET_XML = "ish/oncourse/webservices/soap/QEMoneyVoucherRedeemWithMoneyPaymentSuccessDataSet.xml";

	@Before
	public void before() throws Exception {
		testEnv = new V16TestEnv(DEFAULT_DATASET_XML, null);
		testEnv.start();
	}

	@Override
	protected void prepareStubsForReplication(GenericTransactionGroup transaction, GenericParametersMap parametersMap) {
		preparePaymentStructureForMoneyVoucherAndMoneyPayment(transaction, parametersMap);
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
