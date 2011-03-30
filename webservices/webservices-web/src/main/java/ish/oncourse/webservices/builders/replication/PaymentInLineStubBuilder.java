package ish.oncourse.webservices.builders.replication;

import ish.oncourse.model.PaymentInLine;
import ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub;

public class PaymentInLineStubBuilder extends AbstractWillowStubBuilder<PaymentInLine, PaymentInLineStub> {

	@Override
	protected PaymentInLineStub createFullStub(PaymentInLine entity) {
		PaymentInLineStub stub = new PaymentInLineStub();
		
		stub.setAmount(entity.getAmount());
		stub.setAngelId(entity.getAngelId());
		stub.setCreated(entity.getCreated());
		stub.setInvoiceId(entity.getInvoice().getId());
		stub.setModified(entity.getModified());
		stub.setPaymentInId(entity.getPaymentIn().getId());
		stub.setWillowId(entity.getId());
		
		return stub;
	}
}
