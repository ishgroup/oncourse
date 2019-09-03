package ish.oncourse.webservices.soap.v21;

import ish.oncourse.webservices.soap.v21.PaymentPortType;
import ish.oncourse.webservices.soap.v21.ReplicationFault;
import ish.oncourse.webservices.v21.stubs.replication.ParametersMap;
import ish.oncourse.webservices.v21.stubs.replication.TransactionGroup;

import javax.jws.WebParam;
import javax.jws.WebService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@WebService(endpointInterface = "ish.oncourse.webservices.soap.v21.PaymentPortType", serviceName = "ReplicationService", portName = "PaymentPortType", targetNamespace = "http://repl.v21.soap.webservices.oncourse.ish/")
public class TestPaymentPortTypeImpl implements PaymentPortType {
	@Override
	public TransactionGroup processRefund(@WebParam(partName = "paymentOut", name = "paymentOut", targetNamespace = "") TransactionGroup transactionGroup) throws ish.oncourse.webservices.soap.v21.ReplicationFault {
		return transactionGroup;
	}

	@Override
	public TransactionGroup getPaymentStatus(@WebParam(partName = "sessionId", name = "sessionId", targetNamespace = "") String s) throws ish.oncourse.webservices.soap.v21.ReplicationFault {
		assertEquals("PaymentStatus",s);

		try {
			return AbstractTransportTest.createTransactionGroupWithAllStubs();
		} catch (Throwable throwable) {
			throw new ish.oncourse.webservices.soap.v21.ReplicationFault("",throwable);
		}
	}

	@Override
	public TransactionGroup processPayment(@WebParam(partName = "transaction", name = "transaction", targetNamespace = "") TransactionGroup transactionGroup, @WebParam(partName = "paymentModel", name = "paymentModel", targetNamespace = "") ParametersMap parametersMap) throws ish.oncourse.webservices.soap.v21.ReplicationFault {
		assertNotNull(parametersMap);
		return transactionGroup;
	}

	@Override
	public TransactionGroup getVouchers(@WebParam(partName = "transactionRequest", name = "transactionRequest", targetNamespace = "") TransactionGroup group) throws ish.oncourse.webservices.soap.v21.ReplicationFault {
		return group;
	}

	@Override
	public ParametersMap verifyUSI(@WebParam(partName = "verificationRequest", name = "verificationRequest", targetNamespace = "") ParametersMap parametersMap) throws ReplicationFault {
		return parametersMap;
	}
}
