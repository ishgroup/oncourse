package ish.oncourse.webservices.soap.v14;

import ish.oncourse.webservices.util.GenericParametersMap;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

//@RunWith(RandomizedRunner.class)
public class QEMoneyVoucherRedeemSuccessNoGUITest extends QEVoucherRedeemSuccessNoGUITest {
	private static final String DEFAULT_DATASET_XML = "ish/oncourse/webservices/soap/QEMoneyVoucherRedeemSuccessDataSet.xml";

	@Before
	public void before() throws Exception {
		testEnv = new V14TestEnv(DEFAULT_DATASET_XML, null).start();
	}

	@Override
	protected void prepareStubsForReplication(GenericTransactionGroup transaction, GenericParametersMap parametersMap) {
		prepareMoneyVoucherNoMoneyPayment(transaction, parametersMap);
	}

	@Override
	protected void checkProcessedResponse(GenericTransactionGroup transaction) {
		checkProcessedResponseForVoucherNoGUI(transaction);
	}

	@Override
	protected void checkAsyncReplication(ObjectContext context) {
		checkAsyncReplicationForVoucherNoGUI(context);
	}

	@Test
	public void testQESuccessPayment() throws Exception {
		testNoGUICases();
	}
}
