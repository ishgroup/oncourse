package ish.oncourse.webservices.soap.v6;

import ish.oncourse.webservices.replication.services.IReplicationService.InternalReplicationFault;
import ish.oncourse.webservices.replication.services.InternalPaymentService;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.util.SupportedVersions;
import ish.oncourse.webservices.v6.stubs.replication.TransactionGroup;
import org.apache.cxf.annotations.EndpointProperty;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService(endpointInterface = "ish.oncourse.webservices.soap.v6.PaymentPortType", serviceName = "ReplicationService", portName = "PaymentPortType",
		targetNamespace = "http://repl.v6.soap.webservices.oncourse.ish/")
//Workaround to avoid validation for soap operations body in cxf > 2.6.1. see http://cxf.apache.org/cve-2012-3451.html
@EndpointProperty(key = "soap.no.validate.parts", value = "true")
public class PaymentPortTypeImpl implements PaymentPortType {

	@Inject
	@Autowired
	private InternalPaymentService paymentPort;

	@WebMethod(action = "getPaymentStatus")
	@Override
	public TransactionGroup getPaymentStatus(String sessionId) throws ReplicationFault {
		try {
			return PortHelper.getV6TransactionGroup(paymentPort.getPaymentStatus(sessionId, SupportedVersions.V6));
		} catch (InternalReplicationFault e) {
			throw ReplicationPortTypeImpl.createReplicationFaultForException(e);
		}
	}

	@WebMethod(operationName = "processPayment")
	@Override
	public TransactionGroup processPayment(TransactionGroup transaction) throws ReplicationFault {
		try {
			return PortHelper.getV6TransactionGroup(paymentPort.processPayment(transaction));
		} catch (InternalReplicationFault e) {
			throw ReplicationPortTypeImpl.createReplicationFaultForException(e);
		}
	}

	@WebMethod(operationName = "processRefund")
	@Override
	public TransactionGroup processRefund(TransactionGroup paymentOut) throws ReplicationFault {
		try {
			return PortHelper.getV6TransactionGroup(paymentPort.processRefund(paymentOut));
		} catch (InternalReplicationFault e) {
			throw ReplicationPortTypeImpl.createReplicationFaultForException(e);
		}
	}

	@WebMethod(operationName = "getVouchers")
	@Override
	public TransactionGroup getVouchers(TransactionGroup transactionGroup) throws ReplicationFault {
		try {
			return PortHelper.getV6TransactionGroup(paymentPort.getVouchers(transactionGroup, SupportedVersions.V6));
		} catch (InternalReplicationFault e) {
			throw ReplicationPortTypeImpl.createReplicationFaultForException(e);
		}
	}
}
