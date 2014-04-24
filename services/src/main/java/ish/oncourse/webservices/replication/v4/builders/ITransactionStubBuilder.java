package ish.oncourse.webservices.replication.v4.builders;

import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.webservices.util.SupportedVersions;
import ish.oncourse.webservices.util.GenericReplicationStub;
import java.util.List;
import java.util.Set;

/**
 * @author anton
 */
public interface ITransactionStubBuilder {

	Set<GenericReplicationStub> createPaymentInTransaction(List<PaymentIn> paymentIn, final SupportedVersions version);

	Set<GenericReplicationStub> createRefundTransaction(PaymentOut paymentOut, final SupportedVersions version);
}
