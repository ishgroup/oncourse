package ish.oncourse.webservices.soap.v8;

import ish.oncourse.webservices.soap.v8.PaymentPortType;
import ish.oncourse.webservices.soap.v8.ReplicationFault;
import ish.oncourse.webservices.v8.stubs.replication.ParametersMap;
import ish.oncourse.webservices.v8.stubs.replication.TransactionGroup;

import javax.jws.WebParam;
import javax.jws.WebService;

import static org.junit.Assert.assertEquals;

@WebService(endpointInterface = "ish.oncourse.webservices.soap.v8.PaymentPortType", serviceName = "ReplicationService", portName = "PaymentPortType", targetNamespace = "http://repl.v8.soap.webservices.oncourse.ish/")
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

	@Override
	public ParametersMap verifyUSI(@WebParam(partName = "verificationRequest", name = "verificationRequest", targetNamespace = "") ParametersMap parametersMap) throws ReplicationFault {
		return parametersMap;
	}
}
