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
		stub.setModified(entity.getModified());
		stub.setPrivateNotes(entity.getStatusNotes());
		stub.setSource(entity.getSource().getDatabaseValue());

		switch (entity.getStatus()) {
		case PENDING:
			stub.setStatus(ish.common.types.PaymentStatus.QUEUED.getDatabaseValue());
			break;
		case IN_TRANSACTION:
			stub.setStatus(ish.common.types.PaymentStatus.IN_TRANSACTION.getDatabaseValue());
			break;
		case SUCCESS:
			stub.setStatus(ish.common.types.PaymentStatus.SUCCESS.getDatabaseValue());
			break;
		case FAILED:
			stub.setStatus(ish.common.types.PaymentStatus.FAILED.getDatabaseValue());
			break;
		case REFUNDED:
			stub.setStatus(ish.common.types.PaymentStatus.STATUS_REFUNDED.getDatabaseValue());
			break;
		}
		
		stub.setType(2);

		return stub;
	}
}
