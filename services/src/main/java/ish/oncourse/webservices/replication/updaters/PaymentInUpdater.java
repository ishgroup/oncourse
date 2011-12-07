package ish.oncourse.webservices.replication.updaters;

import ish.common.types.CreditCardType;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.common.types.TypesUtil;
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
		
		PaymentSource source = TypesUtil.getEnumForDatabaseValue(stub.getSource(), PaymentSource.class);
		//PaymentSource source = PaymentSource.getSourceForValue(stub.getSource());
		entity.setSource(source);

		if (stub.getStatus() != null) {
			PaymentStatus st = TypesUtil.getEnumForDatabaseValue(stub.getStatus(), PaymentStatus.class);
			//PaymentStatus st = PaymentStatus.getEnumForDatabaseValue(stub.getStatus());
			entity.setStatus(st);
		}
		
		entity.setGatewayReference(stub.getGatewayReference());
		entity.setGatewayResponse(stub.getGatewayResponse());
		entity.setSessionId(stub.getSessionId());
		
		entity.setCreditCardExpiry(stub.getCreditCardExpiry());
		entity.setCreditCardName(stub.getCreditCardName());
		entity.setCreditCardNumber(stub.getCreditCardNumber());
		
		if (stub.getCreditCardType() != null) {
			entity.setCreditCardType(TypesUtil.getEnumForDatabaseValue(stub.getStatus(), CreditCardType.class));
			//entity.setCreditCardType(CreditCardType.getTypeForDatabaseValue(stub.getCreditCardType()));
		}
		
		entity.setType(TypesUtil.getEnumForDatabaseValue(stub.getType(), PaymentType.class));
		//entity.setType(PaymentType.getEnumForDatabaseValue(stub.getType()));
		
		entity.setDateBanked(stub.getDateBanked());
	}
}
