package ish.oncourse.webservices.replication.v5.updaters;

import ish.oncourse.model.Contact;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.Membership;
import ish.oncourse.model.Product;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v5.stubs.replication.MembershipStub;

public class MembershipUpdater extends AbstractWillowUpdater<MembershipStub, Membership> {

	@Override
	protected void updateEntity(final MembershipStub stub, final Membership entity, final RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setExpiryDate(stub.getExpiryDate());
		Contact contact = callback.updateRelationShip(stub.getContactId(), Contact.class);
		entity.setContact(contact);
		InvoiceLine invoiceLine = callback.updateRelationShip(stub.getInvoiceLineId(), InvoiceLine.class);
		entity.setInvoiceLine(invoiceLine);
		entity.setModified(stub.getModified());
		Product product = callback.updateRelationShip(stub.getProductId(), Product.class);
		entity.setProduct(product);
		entity.setType(stub.getType());
	}

}
