package ish.oncourse.webservices.replication.updaters;

import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.webservices.v4.stubs.replication.PaymentOutStub;

public class PaymentOutUpdater extends AbstractWillowUpdater<PaymentOutStub, PaymentOut> {

	@Override
	protected void updateEntity(PaymentOutStub stub, PaymentOut entity, RelationShipCallback callback) {
		entity.setContact(callback.updateRelationShip(stub.getContactId(), Contact.class));
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setPaymentInTxnReference(stub.getPaymentInTxnReference());
		entity.setStatus(PaymentStatus.getEnumForDatabaseValue(stub.getStatus()));
		entity.setTotalAmount(stub.getAmount());
		
		entity.setDateBanked(stub.getDateBanked());
		entity.setDatePaid(stub.getDatePaid());
		
		if (stub.getSource() != null) {
			PaymentSource source = PaymentSource.getSourceForValue(stub.getSource());
			if (source != null) {
				entity.setSource(source);
			}
		}
		
		if (entity.getSource() == null) {
			entity.setSource(PaymentSource.SOURCE_ONCOURSE);
		}
	}
}
