package ish.oncourse.webservices.soap;

import ish.oncourse.webservices.soap.v4.PaymentPortType;
import ish.oncourse.webservices.soap.v4.ReplicationFault;
import ish.oncourse.webservices.soap.v4.ReplicationPortType;
import ish.oncourse.webservices.soap.v4.ReplicationService;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import javax.xml.ws.BindingProvider;


public class PaymentPortTypeTransportTest extends AbstractTransportTest {


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
		ReplicationService replicationService = new ReplicationService(ReplicationPortType.class.getClassLoader().getResource("wsdl/v4_replication.wsdl"));
		PaymentPortType paymentPortType = replicationService.getPaymentPortType();

		initPortType((BindingProvider) paymentPortType, "/services/v4/payment");
		return paymentPortType;
	}


}
