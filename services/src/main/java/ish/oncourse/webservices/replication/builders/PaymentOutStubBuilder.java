package ish.oncourse.webservices.replication.builders;

import ish.common.types.PaymentType;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.webservices.v4.stubs.replication.PaymentOutStub;

public class PaymentOutStubBuilder extends AbstractWillowStubBuilder<PaymentOut, PaymentOutStub> {

	@Override
	protected PaymentOutStub createFullStub(PaymentOut entity) {
		
		PaymentOutStub stub = new PaymentOutStub();
		
		stub.setAmount(entity.getTotalAmount());
		stub.setContactId(entity.getContact().getId());
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setPaymentInTxnReference(entity.getPaymentInTxnReference());
		stub.setSource(entity.getSource());
		stub.setStatus(entity.getStatus().getDatabaseValue());
		stub.setType(PaymentType.CREDIT_CARD.getDatabaseValue());
		
		stub.setDateBanked(entity.getDateBanked());
		stub.setDatePaid(entity.getDatePaid());
		
		return stub;
	}
}
