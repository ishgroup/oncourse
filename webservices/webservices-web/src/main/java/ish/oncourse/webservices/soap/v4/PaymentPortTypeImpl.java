package ish.oncourse.webservices.soap.v4;

import ish.oncourse.webservices.v4.stubs.replication.PaymentTransaction;
import ish.oncourse.webservices.v4.stubs.replication.TransactionResult;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Holder;

@WebService(endpointInterface = "ish.oncourse.webservices.soap.v4.PaymentPortType", serviceName = "ReplicationService", portName = "PaymentPortType", targetNamespace = "http://repl.v4.soap.webservices.oncourse.ish/")
public class PaymentPortTypeImpl implements PaymentPortType {

	@Override
	@WebMethod(operationName = "processRefund")
	public void processRefund(Holder<TransactionResult> transactionResult) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@WebMethod(operationName = "processPayment")
	public TransactionResult processPayment(
			PaymentTransaction paymentTransaction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@WebMethod(operationName = "reserveEnrolments")
	public void reserveEnrolments(Holder<TransactionResult> transactionResult) {
		// TODO Auto-generated method stub
		
	}
}
