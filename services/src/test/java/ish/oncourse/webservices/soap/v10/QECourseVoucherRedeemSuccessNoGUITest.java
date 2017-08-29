package ish.oncourse.webservices.soap.v10;

import ish.oncourse.webservices.util.GenericParametersMap;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

public class QECourseVoucherRedeemSuccessNoGUITest extends QEVoucherRedeemSuccessNoGUITest {
	private static final String DEFAULT_DATASET_XML = "ish/oncourse/webservices/soap/QECourseVoucherRedeemSuccessDataSet.xml";

	@Before
	public void before() throws Exception {
		testEnv = new V10TestEnv(DEFAULT_DATASET_XML, null);
		testEnv.start();
	}

	@Override
	protected void prepareStubsForReplication(GenericTransactionGroup transaction,GenericParametersMap parametersMap) {
		prepareCourseVoucherNoMoneyPayment(transaction, parametersMap);
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
