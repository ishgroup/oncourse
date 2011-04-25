package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.PaymentIn;
import ish.oncourse.webservices.v4.stubs.replication.PaymentInStub;

public class PaymentInStubBuilder extends AbstractWillowStubBuilder<PaymentIn, PaymentInStub> {

	@Override
	protected PaymentInStub createFullStub(PaymentIn entity) {
		PaymentInStub stub = new PaymentInStub();
		
		stub.setAmount(entity.getAmount());
		stub.setContactId(entity.getContact().getId());
		stub.setCreated(entity.getCreated());
		stub.setCreditCardCVV(entity.getCreditCardCVV());
		stub.setCreditCardExpiry(entity.getCreditCardExpiry());
		stub.setCreditCardName(entity.getCreditCardName());
		stub.setCreditCardNumber(entity.getCreditCardNumber());
		stub.setCreditCardType(entity.getCreditCardType().getDatabaseValue());
		stub.setModified(entity.getModified());
		stub.setStudentId(entity.getStudent().getId());
		stub.setWillowId(entity.getId());
		
		return stub;
	}
}
