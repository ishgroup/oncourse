package ish.oncourse.webservices.builders.replication;

import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.v4.stubs.replication.PaymentInLineStub;

import java.util.Map;

public class PaymentInLineStubBuilder extends AbstractWillowStubBuilder<PaymentInLine, PaymentInLineStub> {
	
	public PaymentInLineStubBuilder(Map<QueuedKey, QueuedRecord> queue, IWillowStubBuilder next) {
		super(queue, next);
	}

	@Override
	protected PaymentInLineStub createFullStub(PaymentInLine entity) {
		PaymentInLineStub stub = new PaymentInLineStub();
		
		stub.setAmount(entity.getAmount());
		stub.setAngelId(entity.getAngelId());
		stub.setCreated(entity.getCreated());
		stub.setInvoice(findRelatedStub(entity.getInvoice()));
		stub.setModified(entity.getModified());
		stub.setPaymentIn(findRelatedStub(entity.getPaymentIn()));
		stub.setWillowId(entity.getId());
		
		return stub;
	}
}
