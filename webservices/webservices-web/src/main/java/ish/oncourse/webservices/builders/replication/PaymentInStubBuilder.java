package ish.oncourse.webservices.builders.replication;

import java.util.Map;

import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.v4.stubs.replication.PaymentInStub;

public class PaymentInStubBuilder extends AbstractWillowStubBuilder<PaymentIn, PaymentInStub> {
	
	public PaymentInStubBuilder(Map<QueuedKey, QueuedRecord> queue, IWillowQueueService queueService, IWillowStubBuilder next) {
		super(queue, queueService, next);
	}

	@Override
	protected PaymentInStub createFullStub(PaymentIn entity) {
		PaymentInStub stub = new PaymentInStub();
		
		stub.setAmount(entity.getAmount());
		stub.setContact(findRelationshipStub(entity.getContact()));
		stub.setCreated(entity.getCreated());
		stub.setCreditCardCVV(entity.getCreditCardCVV());
		stub.setCreditCardExpiry(entity.getCreditCardExpiry());
		stub.setCreditCardName(entity.getCreditCardName());
		stub.setCreditCardNumber(entity.getCreditCardNumber());
		stub.setCreditCardType(entity.getCreditCardType().getDatabaseValue());
		stub.setModified(entity.getModified());
		stub.setStudent(findRelationshipStub(entity.getStudent()));
		stub.setWillowId(entity.getId());
		
		return stub;
	}
}
