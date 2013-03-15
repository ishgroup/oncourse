package ish.oncourse.webservices.replication.v5.updaters;

import ish.common.types.CreditCardType;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.common.types.TypesUtil;
import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v5.stubs.replication.PaymentInStub;

public class PaymentInUpdater extends AbstractWillowUpdater<PaymentInStub, PaymentIn> {

	@Override
	protected void updateEntity(PaymentInStub stub, PaymentIn entity, RelationShipCallback callback) {
		
		entity.setAmount(new Money(stub.getAmount()));
		entity.setContact(callback.updateRelationShip(stub.getContactId(), Contact.class));
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		
		PaymentSource source = TypesUtil.getEnumForDatabaseValue(stub.getSource(), PaymentSource.class);
		entity.setSource(source);

		if (stub.getStatus() != null) {
			PaymentStatus st = TypesUtil.getEnumForDatabaseValue(stub.getStatus(), PaymentStatus.class);
			entity.setStatus(st);
		}
		
		entity.setSessionId(stub.getSessionId());
		
		if (stub.getCreditCardType() != null) {
			entity.setCreditCardType(TypesUtil.getEnumForDatabaseValue(stub.getStatus(), CreditCardType.class));
		}
		
		entity.setType(TypesUtil.getEnumForDatabaseValue(stub.getType(), PaymentType.class));
		entity.setDateBanked(stub.getDateBanked());
	}
}
