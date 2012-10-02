package ish.oncourse.webservices.replication.services;

import java.util.List;

import org.apache.cayenne.ObjectId;

import ish.oncourse.model.PaymentIn;
import ish.oncourse.webservices.replication.services.IReplicationService.InternalReplicationFault;
import ish.oncourse.webservices.util.GenericTransactionGroup;

public interface InternalPaymentService {
	public GenericTransactionGroup processPayment(GenericTransactionGroup transaction) throws InternalReplicationFault;
	public GenericTransactionGroup processRefund(GenericTransactionGroup paymentOut) throws InternalReplicationFault;
	public GenericTransactionGroup getPaymentStatus(String sessionId, final SupportedVersions version) throws InternalReplicationFault;
	boolean isHaveConflictedInInvoices(PaymentIn paymentIn, List<PaymentIn> updatedPayments);
	List<ObjectId> getLinesForConflictedInvoices(PaymentIn paymentForCheck);
}
