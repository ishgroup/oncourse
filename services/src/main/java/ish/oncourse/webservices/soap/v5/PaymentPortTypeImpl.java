package ish.oncourse.webservices.soap.v5;

import ish.oncourse.webservices.replication.services.IReplicationService.InternalReplicationFault;
import ish.oncourse.webservices.replication.services.InternalPaymentService;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.util.SupportedVersions;
import ish.oncourse.webservices.v5.stubs.replication.TransactionGroup;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService(endpointInterface = "ish.oncourse.webservices.soap.v5.PaymentPortType", serviceName = "ReplicationService", portName = "PaymentPortType", 
	targetNamespace = "http://repl.v5.soap.webservices.oncourse.ish/")
@Deprecated
public class PaymentPortTypeImpl implements PaymentPortType {

	@Inject
	@Autowired
	private InternalPaymentService paymentPort;
	
	@WebMethod(action = "getPaymentStatus")
	@Override
	public TransactionGroup getPaymentStatus(String sessionId) throws ReplicationFault {
		try {
			return PortHelper.getV5TransactionGroup(paymentPort.getPaymentStatus(sessionId, SupportedVersions.V5));
		} catch (InternalReplicationFault e) {
			throw ReplicationPortTypeImpl.createReplicationFaultForException(e);
		}
	}

	@WebMethod(operationName = "processPayment")
	@Override
	public TransactionGroup processPayment(TransactionGroup transaction) throws ReplicationFault {
		try {
			return PortHelper.getV5TransactionGroup(paymentPort.processPayment(transaction));
		} catch (InternalReplicationFault e) {
			throw ReplicationPortTypeImpl.createReplicationFaultForException(e);
		}
	}

	@WebMethod(operationName = "processRefund")
	@Override
	public TransactionGroup processRefund(TransactionGroup paymentOut) throws ReplicationFault {
		try {
			return PortHelper.getV5TransactionGroup(paymentPort.processRefund(paymentOut));
		} catch (InternalReplicationFault e) {
			throw ReplicationPortTypeImpl.createReplicationFaultForException(e);
		}
	}
}
