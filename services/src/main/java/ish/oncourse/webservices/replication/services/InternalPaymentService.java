package ish.oncourse.webservices.replication.services;

import ish.oncourse.webservices.replication.services.IReplicationService.InternalReplicationFault;
import ish.oncourse.webservices.util.GenericParametersMap;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.util.SupportedVersions;

public interface InternalPaymentService {
	GenericTransactionGroup getVouchers(GenericTransactionGroup transactionGroup, SupportedVersions version) throws InternalReplicationFault;
	GenericParametersMap verifyCheckout(GenericParametersMap verificationRequest,  SupportedVersions version) throws InternalReplicationFault;
}
