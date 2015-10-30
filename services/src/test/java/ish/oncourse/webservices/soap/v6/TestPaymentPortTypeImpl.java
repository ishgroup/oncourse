package ish.oncourse.webservices.soap.v6;

import ish.oncourse.webservices.soap.AbstractTransportTest;
import ish.oncourse.webservices.v6.stubs.replication.TransactionGroup;
import org.apache.cxf.annotations.EndpointProperty;

import javax.jws.WebParam;
import javax.jws.WebService;

import static org.junit.Assert.assertEquals;

@WebService(endpointInterface = "ish.oncourse.webservices.soap.v6.PaymentPortType", serviceName = "ReplicationService", portName = "PaymentPortType", targetNamespace = "http://repl.v6.soap.webservices.oncourse.ish/")
@EndpointProperty(key = "soap.no.validate.parts", value = "true")
public class TestPaymentPortTypeImpl implements PaymentPortType {
	@Override
	public TransactionGroup processRefund(@WebParam(partName = "paymentOut", name = "paymentOut", targetNamespace = "") TransactionGroup transactionGroup) throws ReplicationFault {
		return transactionGroup;
	}

	@Override
	public TransactionGroup getPaymentStatus(@WebParam(partName = "sessionId", name = "sessionId", targetNamespace = "") String s) throws ReplicationFault {
		assertEquals("PaymentStatus",s);

		try {
			return AbstractTransportTest.createTransactionGroupWithAllStubs();
		} catch (Throwable throwable) {
			throw new ReplicationFault("",throwable);
		}
	}

	@Override
	public TransactionGroup processPayment(@WebParam(partName = "transaction", name = "transaction", targetNamespace = "") TransactionGroup transactionGroup) throws ReplicationFault {
		return transactionGroup;
	}

	@Override
	public TransactionGroup getVouchers(@WebParam(partName = "transactionRequest", name = "transactionRequest", targetNamespace = "") TransactionGroup group) throws ReplicationFault {
		return group;
	}
}
