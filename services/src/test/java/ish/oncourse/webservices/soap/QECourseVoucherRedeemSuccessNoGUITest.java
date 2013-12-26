package ish.oncourse.webservices.soap;

import ish.oncourse.webservices.util.GenericTransactionGroup;
import org.apache.cayenne.ObjectContext;
import org.junit.BeforeClass;
import org.junit.Test;

public class QECourseVoucherRedeemSuccessNoGUITest extends CommonRealWSTransportTest {
	private static final String DEFAULT_DATASET_XML = "ish/oncourse/webservices/soap/QECourseVoucherRedeemSuccessDataSet.xml";
	private static TestServer server;

	@Override
	protected TestServer getServer() {
		return server;
	}

	@BeforeClass
	public static void initTestServer() throws Exception {
		server = startRealWSServer(9102);
	}

	@Override
	protected String getDataSetFile() {
		return DEFAULT_DATASET_XML;
	}

	@Override
	protected GenericTransactionGroup prepareStubsForReplication(GenericTransactionGroup transaction) {
		return prepareCourseVoucherNoMoneyPayment(transaction);
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
