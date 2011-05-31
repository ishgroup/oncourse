package ish.oncourse.webservices.replication.updaters;

import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.webservices.v4.stubs.replication.PaymentInStub;

public class PaymentInUpdater extends AbstractWillowUpdater<PaymentInStub, PaymentIn> {

	@Override
	protected void updateEntity(PaymentInStub stub, PaymentIn entity, RelationShipCallback callback) {
		entity.setAmount(stub.getAmount());
		entity.setContact(callback.updateRelationShip(stub.getContactId(), Contact.class));
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setSource(PaymentSource.getSourceForValue(stub.getSource()));

		if (stub.getStatus() != null) {
			PaymentStatus st = PaymentStatus.getEnumForDatabaseValue(stub.getStatus());

			switch (st) {
			case SUCCESS:
				entity.setStatus(ish.oncourse.model.PaymentStatus.SUCCESS);
			case QUEUED:
				entity.setStatus(ish.oncourse.model.PaymentStatus.PENDING);
			case FAILED:
				entity.setStatus(ish.oncourse.model.PaymentStatus.FAILED);
			}
		}

	}
}
