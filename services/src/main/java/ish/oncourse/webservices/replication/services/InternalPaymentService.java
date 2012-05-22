package ish.oncourse.webservices.replication.services;

import ish.oncourse.webservices.replication.services.IReplicationService.InternalReplicationFault;
import ish.oncourse.webservices.util.GenericTransactionGroup;

public interface InternalPaymentService {
	public GenericTransactionGroup processPayment(GenericTransactionGroup transaction) throws InternalReplicationFault;
	public GenericTransactionGroup processRefund(GenericTransactionGroup paymentOut) throws InternalReplicationFault;
	public GenericTransactionGroup getPaymentStatus(String sessionId, final SupportedVersions version) throws InternalReplicationFault;
}
