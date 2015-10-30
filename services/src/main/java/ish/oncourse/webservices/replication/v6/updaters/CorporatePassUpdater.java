package ish.oncourse.webservices.replication.v6.updaters;

import ish.oncourse.model.Contact;
import ish.oncourse.model.CorporatePass;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v6.stubs.replication.CorporatePassStub;

public class CorporatePassUpdater extends AbstractWillowUpdater<CorporatePassStub, CorporatePass> {

	@Override
	protected void updateEntity(CorporatePassStub stub, CorporatePass entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setExpiryDate(stub.getExpiryDate());
		entity.setInvoiceEmail(stub.getInvoiceEmail());
		entity.setPassword(stub.getPassword());
		entity.setContact(callback.updateRelationShip(stub.getContactId(), Contact.class));
	}

}
