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
		return null;
	}
}
