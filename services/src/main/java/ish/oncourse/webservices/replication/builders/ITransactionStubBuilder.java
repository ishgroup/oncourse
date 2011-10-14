package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

import java.util.List;
import java.util.Set;

/**
 * @author anton
 */
public interface ITransactionStubBuilder {

	Set<ReplicationStub> createPaymentInTransaction(List<PaymentIn> paymentIn);

	Set<ReplicationStub> createRefundTransaction(PaymentOut paymentOut);
}
