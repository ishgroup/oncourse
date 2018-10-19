package ish.oncourse.webservices.soap.v18;

import ish.oncourse.webservices.replication.services.IReplicationService.InternalReplicationFault;
import ish.oncourse.webservices.replication.services.InternalPaymentService;
import ish.oncourse.webservices.soap.v18.PaymentPortType;
import ish.oncourse.webservices.soap.v18.ReplicationFault;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.util.SupportedVersions;
import ish.oncourse.webservices.v18.stubs.replication.ParametersMap;
import ish.oncourse.webservices.v18.stubs.replication.TransactionGroup;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService(endpointInterface = "ish.oncourse.webservices.soap.v18.PaymentPortType", serviceName = "ReplicationService", portName = "PaymentPortType",
		targetNamespace = "http://repl.v18.soap.webservices.oncourse.ish/")
public class PaymentPortTypeImpl implements PaymentPortType {

	@Inject
	@Autowired
	private InternalPaymentService paymentPort;

	@WebMethod(action = "getPaymentStatus")
	@Override
	public TransactionGroup getPaymentStatus(String sessionId) throws ReplicationFault {
		try {
			return PortHelper.getV18TransactionGroup(paymentPort.getPaymentStatus(sessionId, SupportedVersions.V18));
		} catch (InternalReplicationFault e) {
			throw ReplicationPortTypeImpl.createReplicationFaultForException(e);
		}
	}

	@WebMethod(operationName = "processRefund")
	@Override
	public TransactionGroup processRefund(TransactionGroup paymentOut) throws ReplicationFault {
		try {
			return PortHelper.getV18TransactionGroup(paymentPort.processRefund(paymentOut));
		} catch (InternalReplicationFault e) {
			throw ReplicationPortTypeImpl.createReplicationFaultForException(e);
		}
	}

	@WebMethod(operationName = "getVouchers")
	@Override
	public TransactionGroup getVouchers(TransactionGroup transactionGroup) throws ReplicationFault {
		try {
			return PortHelper.getV18TransactionGroup(paymentPort.getVouchers(transactionGroup, SupportedVersions.V18));
		} catch (InternalReplicationFault e) {
			throw ReplicationPortTypeImpl.createReplicationFaultForException(e);
		}
	}
	
	@WebMethod(operationName = "verifyUSI")
	@Override
	public ParametersMap verifyUSI(ParametersMap parametersMap) throws ReplicationFault {
		try {
			return (ParametersMap) paymentPort.verifyUSI(parametersMap);
		} catch (Exception e) {
			throw ReplicationPortTypeImpl.createReplicationFaultForException(e);
		}
	}
	
	@WebMethod(operationName = "processPayment")
	@Override
	public TransactionGroup processPayment(TransactionGroup transactionGroup, ParametersMap parametersMap) throws ReplicationFault {
		try {
			return PortHelper.getV18TransactionGroup(paymentPort.processPayment(transactionGroup, parametersMap));
		} catch (InternalReplicationFault e) {
			throw ReplicationPortTypeImpl.createReplicationFaultForException(e);
		}
	}
}
