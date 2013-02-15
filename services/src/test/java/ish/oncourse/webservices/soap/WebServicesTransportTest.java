package ish.oncourse.webservices.soap;

import ish.oncourse.webservices.soap.v4.*;
import ish.oncourse.webservices.v4.stubs.reference.ReferenceResult;
import ish.oncourse.webservices.v4.stubs.reference.ReferenceStub;
import ish.oncourse.webservices.v4.stubs.replication.InstructionStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationRecords;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationResult;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import javax.xml.ws.BindingProvider;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 */
public class WebServicesTransportTest extends AbstractTransportTest {

	@BeforeClass
	public static void before() throws Exception {
		startServer();
	}

	@AfterClass
	public static void after() throws Exception {
		stopServer();
	}
	
	@Test
	public void testReplicationPortType_authenticate() throws Exception {

		ReplicationPortType replicationPortType = getReplicationPortType();
		replicationPortType.authenticate("securityCode", Long.MAX_VALUE);
	}

	@Test
	public void testReplicationPortType_confirmExecution() throws Exception {

		ReplicationPortType replicationPortType = getReplicationPortType();
		replicationPortType.confirmExecution(Long.MAX_VALUE, "confirmExecution");
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
	public void testReplicationPortType_logout() throws Exception {
		ReplicationPortType replicationPortType = getReplicationPortType();
		replicationPortType.logout(Long.MAX_VALUE);
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
	public void  test_getMaximumVersion() throws JAXBException {
		assertEquals(Long.MAX_VALUE, getReferencePortType().getMaximumVersion());
	}


	@Test
	public void  test_getRecords() throws JAXBException {
		ReferenceResult referenceResult = getReferencePortType().getRecords(Long.MAX_VALUE);
		List<ReferenceStub> resultStub = referenceResult.getCountryOrLanguageOrModule();
		assertListStubs(resultStub,PACKAGE_NAME_REFERENCE_STUBS,ReferenceStub.class);
	}

	@Test
	public void test_processRefund() throws Throwable {
		TransactionGroup transactionGroup = createTransactionGroupWithAllStubs();
		TransactionGroup transactionGroupResult = getPortType().processRefund(transactionGroup);
		assertTransactionGroup(transactionGroupResult);
	}

	@Test
	public void test_getPaymentStatus() throws JAXBException, ReplicationFault {
		TransactionGroup transactionGroupResult = getPortType().getPaymentStatus("PaymentStatus");
		assertTransactionGroup(transactionGroupResult);
	}

	@Test
	public void test_processPayment() throws Throwable {
		TransactionGroup transactionGroup = createTransactionGroupWithAllStubs();
		TransactionGroup transactionGroupResult = getPortType().processPayment(transactionGroup);
		assertTransactionGroup(transactionGroupResult);
	}

	private PaymentPortType getPortType() throws JAXBException {
		return getPaymentPortType("wsdl/v4_replication.wsdl", "/services/v4/payment");
	}

	private ReplicationPortType getReplicationPortType() throws JAXBException {
		return getReplicationPortType("wsdl/v4_replication.wsdl", "/services/v4/replication");
	}
	
	private ReferencePortType getReferencePortType() throws JAXBException {
		return getReferencePortType("wsdl/v4_reference.wsdl", "/services/v4/reference");
	}
}
