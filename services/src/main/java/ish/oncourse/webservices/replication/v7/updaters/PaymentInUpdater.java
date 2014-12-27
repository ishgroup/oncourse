package ish.oncourse.webservices.replication.v7.updaters;

import ish.common.types.*;
import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v7.stubs.replication.PaymentInStub;

public class PaymentInUpdater extends AbstractWillowUpdater<PaymentInStub, PaymentIn> {

	@Override
	protected void updateEntity(PaymentInStub stub, PaymentIn entity, RelationShipCallback callback) {
		entity.setAmount(new Money(stub.getAmount()));
		entity.setContact(callback.updateRelationShip(stub.getContactId(), Contact.class));
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setSource(TypesUtil.getEnumForDatabaseValue(stub.getSource(), PaymentSource.class));
		if (stub.getStatus() != null) {
			entity.setStatus(TypesUtil.getEnumForDatabaseValue(stub.getStatus(), PaymentStatus.class));
		}
		entity.setSessionId(stub.getSessionId());
		if (stub.getCreditCardType() != null) {
			entity.setCreditCardType(TypesUtil.getEnumForDatabaseValue(stub.getStatus(), CreditCardType.class));
		}
		entity.setType(TypesUtil.getEnumForDatabaseValue(stub.getType(), PaymentType.class));
		entity.setDateBanked(stub.getDateBanked());
	}
}
