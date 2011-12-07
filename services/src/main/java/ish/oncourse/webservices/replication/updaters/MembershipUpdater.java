package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Contact;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.Membership;
import ish.oncourse.model.Product;
import ish.oncourse.webservices.v4.stubs.replication.MembershipStub;

public class MembershipUpdater extends AbstractWillowUpdater<MembershipStub, Membership> {

	@Override
	protected void updateEntity(final MembershipStub stub, final Membership entity, final RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setExpiryDate(stub.getExpiryDate());
		final Contact contact = callback.updateRelationShip(stub.getContactId(), Contact.class);
		entity.setContact(contact);
		final InvoiceLine invoiceLine = callback.updateRelationShip(stub.getInvoiceLineId(), InvoiceLine.class);
		entity.setInvoiceLine(invoiceLine);
		entity.setModified(stub.getModified());
		final Product product = callback.updateRelationShip(stub.getProductId(), Product.class);
		entity.setProduct(product);
		entity.setType(stub.getType());
	}

}
