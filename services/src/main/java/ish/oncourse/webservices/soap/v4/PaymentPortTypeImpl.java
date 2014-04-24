package ish.oncourse.webservices.soap.v4;

import ish.oncourse.webservices.replication.services.IReplicationService.InternalReplicationFault;
import ish.oncourse.webservices.replication.services.InternalPaymentService;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.util.SupportedVersions;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This decorator was created as workaround for
 * InjectService("Atomic/NotAtomic") which doesn't work by default with
 * tapestry5-spring @Autowired and I don't like to create additional useless
 * interface.
 * 
 * @author anton
 * 
 */

@WebService(endpointInterface = "ish.oncourse.webservices.soap.v4.PaymentPortType", serviceName = "ReplicationService", portName = "PaymentPortType", targetNamespace = "http://repl.v4.soap.webservices.oncourse.ish/")
public class PaymentPortTypeImpl implements PaymentPortType {

	@Inject
	@Autowired
	private InternalPaymentService paymentPort;

	@Override
	@WebMethod(operationName = "processRefund")
	public TransactionGroup processRefund(TransactionGroup paymentOut) throws ReplicationFault {
		try {
			return PortHelper.getV4TransactionGroup(paymentPort.processRefund(paymentOut));
		} catch (InternalReplicationFault e) {
			throw ReplicationPortTypeImpl.createReplicationFaultForException(e);
		}
	}

	@Override
	@WebMethod(operationName = "processPayment")
	public TransactionGroup processPayment(TransactionGroup transaction) throws ReplicationFault {
		try {
			return PortHelper.getV4TransactionGroup(paymentPort.processPayment(transaction));
		} catch (InternalReplicationFault e) {
			throw ReplicationPortTypeImpl.createReplicationFaultForException(e);
		}
	}

	@Override
	@WebMethod(action = "getPaymentStatus")
	public TransactionGroup getPaymentStatus(String sessionId) throws ReplicationFault {
		try {
			return PortHelper.getV4TransactionGroup(paymentPort.getPaymentStatus(sessionId, SupportedVersions.V4));
		} catch (InternalReplicationFault e) {
			throw ReplicationPortTypeImpl.createReplicationFaultForException(e);
		}
	}
}
