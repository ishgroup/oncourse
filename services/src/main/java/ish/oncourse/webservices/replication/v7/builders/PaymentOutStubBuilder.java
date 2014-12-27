package ish.oncourse.webservices.replication.v7.builders;

import ish.common.types.PaymentSource;
import ish.common.types.PaymentType;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v7.stubs.replication.PaymentOutStub;

public class PaymentOutStubBuilder extends AbstractWillowStubBuilder<PaymentOut, PaymentOutStub> {

	@Override
	protected PaymentOutStub createFullStub(PaymentOut entity) {
		PaymentOutStub stub = new PaymentOutStub();
		stub.setAmount(entity.getTotalAmount().toBigDecimal());
		stub.setContactId(entity.getContact().getId());
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setPaymentInTxnReference(entity.getPaymentInTxnReference());
		if (entity.getSource() != null) {
			stub.setSource(entity.getSource().getDatabaseValue());
		} else {
			stub.setSource(PaymentSource.SOURCE_WEB.getDatabaseValue());
		}
		stub.setStatus(entity.getStatus().getDatabaseValue());
		stub.setType(PaymentType.CREDIT_CARD.getDatabaseValue());
		stub.setDateBanked(entity.getDateBanked());
		stub.setDatePaid(entity.getDatePaid());
		return stub;
	}
}
