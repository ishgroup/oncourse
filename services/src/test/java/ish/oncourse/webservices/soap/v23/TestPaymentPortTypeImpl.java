package ish.oncourse.webservices.soap.v23;

import ish.oncourse.webservices.v23.stubs.replication.ParametersMap;
import ish.oncourse.webservices.v23.stubs.replication.TransactionGroup;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(endpointInterface = "ish.oncourse.webservices.soap.v23.PaymentPortType", serviceName = "ReplicationService", portName = "PaymentPortType", targetNamespace = "http://repl.v23.soap.webservices.oncourse.ish/")
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
