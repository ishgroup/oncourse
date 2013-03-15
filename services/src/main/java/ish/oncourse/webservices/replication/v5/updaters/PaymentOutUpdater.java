package ish.oncourse.webservices.replication.v5.updaters;

import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentOut;
import ish.common.types.TypesUtil;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v5.stubs.replication.PaymentOutStub;

public class PaymentOutUpdater extends AbstractWillowUpdater<PaymentOutStub, PaymentOut> {

	@Override
	protected void updateEntity(PaymentOutStub stub, PaymentOut entity, RelationShipCallback callback) {
		entity.setContact(callback.updateRelationShip(stub.getContactId(), Contact.class));
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setPaymentInTxnReference(stub.getPaymentInTxnReference());
		
		entity.setStatus(TypesUtil.getEnumForDatabaseValue(stub.getStatus(), PaymentStatus.class));
		entity.setTotalAmount(new Money(stub.getAmount()));
		
		entity.setDateBanked(stub.getDateBanked());
		entity.setDatePaid(stub.getDatePaid());
		
		if (stub.getSource() != null) {
			PaymentSource source = TypesUtil.getEnumForDatabaseValue(stub.getSource(), PaymentSource.class);
			if (source != null) {
				entity.setSource(source);
			}
		}
		
		if (entity.getSource() == null) {
			entity.setSource(PaymentSource.SOURCE_ONCOURSE);
		}
	}
}
