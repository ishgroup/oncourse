package ish.oncourse.webservices.soap.v25;

import ish.oncourse.webservices.replication.services.IReplicationService.InternalReplicationFault;
import ish.oncourse.webservices.replication.services.InternalPaymentService;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.util.SupportedVersions;
import ish.oncourse.webservices.v25.stubs.replication.ParametersMap;
import ish.oncourse.webservices.v25.stubs.replication.TransactionGroup;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService(endpointInterface = "ish.oncourse.webservices.soap.v25.PaymentPortType", serviceName = "ReplicationService", portName = "PaymentPortType",
		targetNamespace = "http://repl.v25.soap.webservices.oncourse.ish/")
public class PaymentPortTypeImpl implements PaymentPortType {

	@Inject
	@Autowired
	private InternalPaymentService paymentPort;

	@WebMethod(operationName = "getVouchers")
	@Override
	public TransactionGroup getVouchers(TransactionGroup transactionGroup) throws ReplicationFault {
		try {
			return PortHelper.getV25TransactionGroup(paymentPort.getVouchers(transactionGroup, SupportedVersions.V25));
		} catch (InternalReplicationFault e) {
			throw ReplicationPortTypeImpl.createReplicationFaultForException(e);
		}
	}

	@Override
	public ParametersMap verifyCheckout(ParametersMap verificationRequest) throws ReplicationFault {
		try {
			return (ParametersMap) paymentPort.verifyCheckout(verificationRequest, SupportedVersions.V25);
		} catch (Exception e) {
			throw ReplicationPortTypeImpl.createReplicationFaultForException(e);
		}
	}
}
