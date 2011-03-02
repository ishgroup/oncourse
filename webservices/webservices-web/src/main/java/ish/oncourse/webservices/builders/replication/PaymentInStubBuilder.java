package ish.oncourse.webservices.builders.replication;

import java.util.Map;

import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.v4.stubs.replication.PaymentInStub;

public class PaymentInStubBuilder extends AbstractWillowStubBuilder<PaymentIn, PaymentInStub> {
	
	public PaymentInStubBuilder(Map<QueuedKey, QueuedRecord> queue, IWillowStubBuilder next) {
		super(queue, next);
	}

	@Override
	protected PaymentInStub createFullStub(PaymentIn entity) {
		PaymentInStub stub = new PaymentInStub();
		
		stub.setAmount(entity.getAmount());
		stub.setContact(findRelatedStub(entity.getContact()));
		stub.setCreated(entity.getCreated());
		stub.setCreditCardCVV(entity.getCreditCardCVV());
		stub.setCreditCardExpiry(entity.getCreditCardExpiry());
		stub.setCreditCardName(entity.getCreditCardName());
		stub.setCreditCardNumber(entity.getCreditCardNumber());
		stub.setCreditCardType(entity.getCreditCardType().getDatabaseValue());
		stub.setModified(entity.getModified());
		stub.setStudent(findRelatedStub(entity.getStudent()));
		stub.setWillowId(entity.getId());
		
		return stub;
	}
}
