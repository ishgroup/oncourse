package ish.oncourse.webservices.soap.v11;

import ish.oncourse.webservices.soap.v11.PaymentPortType;
import ish.oncourse.webservices.soap.v11.ReplicationFault;
import ish.oncourse.webservices.v11.stubs.replication.ParametersMap;
import ish.oncourse.webservices.v11.stubs.replication.TransactionGroup;

import javax.jws.WebParam;
import javax.jws.WebService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@WebService(endpointInterface = "ish.oncourse.webservices.soap.v11.PaymentPortType", serviceName = "ReplicationService", portName = "PaymentPortType", targetNamespace = "http://repl.v11.soap.webservices.oncourse.ish/")
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
	public TransactionGroup processPayment(@WebParam(partName = "transaction", name = "transaction", targetNamespace = "") TransactionGroup transactionGroup, @WebParam(partName = "paymentModel", name = "paymentModel", targetNamespace = "") ParametersMap parametersMap) throws ReplicationFault {
		assertNotNull(parametersMap);
		return transactionGroup;
	}

	@Override
	public TransactionGroup getVouchers(@WebParam(partName = "transactionRequest", name = "transactionRequest", targetNamespace = "") TransactionGroup group) throws ReplicationFault {
		return group;
	}

	@Override
	public ParametersMap verifyUSI(@WebParam(partName = "verificationRequest", name = "verificationRequest", targetNamespace = "") ParametersMap parametersMap) throws ReplicationFault {
		return parametersMap;
	}
}
