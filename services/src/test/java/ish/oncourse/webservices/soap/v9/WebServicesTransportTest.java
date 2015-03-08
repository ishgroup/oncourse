package ish.oncourse.webservices.soap.v9;

import ish.oncourse.webservices.util.Sent2WillowInterceptor;
import ish.oncourse.webservices.v4.stubs.reference.ReferenceResult;
import ish.oncourse.webservices.v4.stubs.reference.ReferenceStub;
import ish.oncourse.webservices.v9.stubs.replication.InstructionStub;
import ish.oncourse.webservices.v9.stubs.replication.ReplicationRecords;
import ish.oncourse.webservices.v9.stubs.replication.ReplicationResult;
import ish.oncourse.webservices.v9.stubs.replication.TransactionGroup;
import org.apache.cxf.endpoint.Client;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import javax.xml.ws.BindingProvider;
import java.util.List;

import static org.junit.Assert.*;

/**
 */
public class WebServicesTransportTest extends AbstractTransportTest {

	private static TestServer server;

    private Sent2WillowInterceptor sent2WillowInterceptor  = new Sent2WillowInterceptor();

    @Override
    public Client initPortType(BindingProvider bindingProvider, String url) throws JAXBException {
        Client client = super.initPortType(bindingProvider, url);
        client.getOutInterceptors().add(sent2WillowInterceptor);
        return client;
    }


    @BeforeClass
	public static void before() throws Exception {
        server = startServer();
	}

	@AfterClass
	public static void after() throws Exception {
		stopServer(server);
	}
	
	@Override
	protected TestServer getServer() {
		return server;
	}

	@Test
	public void testReplicationPortType_authenticate() throws Exception {

		ReplicationPortType replicationPortType = getReplicationPortType();
		replicationPortType.authenticate(getSecurityCode(), getCommunicationKey());
	}

	@Test
	public void testReplicationPortType_confirmExecution() throws Exception {

		ReplicationPortType replicationPortType = getReplicationPortType();
		replicationPortType.confirmExecution(getCommunicationKey(), "confirmExecution");
	}

	@Test
	public void testReplicationPortType_getRecords() throws Exception {
		ReplicationPortType replicationPortType = getReplicationPortType();
		ReplicationRecords replicationRecords = replicationPortType.getRecords();
		assertNotNull(replicationRecords);
		TransactionGroup transactionGroup = replicationRecords.getGroups().get(0);
		assertTransactionGroup(transactionGroup);
	}

	@Test
	public void testReplicationPortType_sendRecords() throws Exception {
		ReplicationPortType replicationPortType = getReplicationPortType();
		ReplicationRecords replicationRecords = new ReplicationRecords();
		ReplicationResult replicationResult = replicationPortType.sendRecords(replicationRecords);
		assertNotNull(replicationResult);
		assertEquals(1,replicationResult.getReplicatedRecord().size());
	}

	@Test
	public void testReplicationPortType_sendResults() throws Exception {
		ReplicationPortType replicationPortType = getReplicationPortType();
		ReplicationResult replicationRecords = new ReplicationResult();
		int result = replicationPortType.sendResults(replicationRecords);
		assertEquals(Integer.MAX_VALUE,result);
	}

	@Test
	public void testReplicationPortType_getInstructions() throws Exception {
		ReplicationPortType replicationPortType = getReplicationPortType();
		List<InstructionStub> result = replicationPortType.getInstructions();
		assertNotNull(result);
	}

	@Test
	public void  testReferencePortType_getMaximumVersion() throws JAXBException {
		assertEquals(Long.MAX_VALUE, getReferencePortType().getMaximumVersion());
	}


	@Test
	public void  testReferencePortType_getRecords() throws JAXBException {
		ReferenceResult referenceResult = getReferencePortType().getRecords(Long.MAX_VALUE);
		List<ReferenceStub> resultStub = referenceResult.getCountryOrLanguageOrModule();
		assertListStubs(resultStub,PACKAGE_NAME_REFERENCE_STUBS,ReferenceStub.class);
	}

	@Test
	public void test_processRefund() throws Throwable {
		TransactionGroup transactionGroup = createTransactionGroupWithAllStubs();
		TransactionGroup transactionGroupResult = getPaymentPortType().processRefund(transactionGroup);
		assertTransactionGroup(transactionGroupResult);
	}

	@Test
	public void test_getPaymentStatus() throws JAXBException, ReplicationFault {
		TransactionGroup transactionGroupResult = getPaymentPortType().getPaymentStatus("PaymentStatus");
		assertTransactionGroup(transactionGroupResult);
	}

	@Test
	public void test_processPayment() throws Throwable {
		TransactionGroup transactionGroup = createTransactionGroupWithAllStubs();
		TransactionGroup transactionGroupResult = getPaymentPortType().processPayment(transactionGroup);
		assertTransactionGroup(transactionGroupResult);
	}

	@Test
	public void test_getVouchers() throws Throwable {
		TransactionGroup transactionGroup = createTransactionGroupWithAllStubs();
		TransactionGroup transactionGroupResult = getPaymentPortType().getVouchers(transactionGroup);
		assertTransactionGroup(transactionGroupResult);
	}

    @Test
    public void test() {
        try {
            getPaymentPortType().getPaymentStatus("PaymentStatus");
            assertTrue(sent2WillowInterceptor.isSent());
        } catch (ReplicationFault replicationFault) {
            throw new RuntimeException(replicationFault);
        }
    }

}
