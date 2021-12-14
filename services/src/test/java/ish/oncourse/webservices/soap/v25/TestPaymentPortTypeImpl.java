package ish.oncourse.webservices.soap.v25;

import ish.oncourse.webservices.v25.stubs.replication.ParametersMap;
import ish.oncourse.webservices.v25.stubs.replication.TransactionGroup;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(endpointInterface = "ish.oncourse.webservices.soap.v25.PaymentPortType", serviceName = "ReplicationService", portName = "PaymentPortType", targetNamespace = "http://repl.v25.soap.webservices.oncourse.ish/")
public class TestPaymentPortTypeImpl implements PaymentPortType {


	@Override
	public TransactionGroup getVouchers(@WebParam(partName = "transactionRequest", name = "transactionRequest", targetNamespace = "") TransactionGroup group) throws ReplicationFault {
		return group;
	}


	@Override
	public ParametersMap verifyCheckout(ParametersMap verificationRequest) throws ReplicationFault {
		return null;
	}
}
