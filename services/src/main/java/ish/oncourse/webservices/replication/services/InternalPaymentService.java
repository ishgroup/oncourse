package ish.oncourse.webservices.replication.services;

import ish.oncourse.webservices.replication.services.IReplicationService.InternalReplicationFault;
import ish.oncourse.webservices.util.GenericParametersMap;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.util.SupportedVersions;

public interface InternalPaymentService {
	GenericTransactionGroup processPayment(GenericTransactionGroup transaction, GenericParametersMap parametersMap) throws InternalReplicationFault;
	GenericTransactionGroup processPayment(GenericTransactionGroup transaction) throws InternalReplicationFault;
	GenericTransactionGroup processRefund(GenericTransactionGroup paymentOut) throws InternalReplicationFault;
	GenericTransactionGroup getPaymentStatus(String sessionId, final SupportedVersions version) throws InternalReplicationFault;
	GenericTransactionGroup getVouchers(GenericTransactionGroup transactionGroup, SupportedVersions version) throws InternalReplicationFault;
	GenericParametersMap verifyUSI(GenericParametersMap parametersMap) throws InternalReplicationFault;
	GenericParametersMap verifyCheckout(GenericParametersMap verificationRequest,  SupportedVersions version) throws InternalReplicationFault;
}
