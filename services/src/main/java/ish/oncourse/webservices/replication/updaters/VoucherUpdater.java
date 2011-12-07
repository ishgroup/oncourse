package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Contact;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.Product;
import ish.oncourse.model.Voucher;
import ish.oncourse.webservices.v4.stubs.replication.VoucherStub;

public class VoucherUpdater extends AbstractWillowUpdater<VoucherStub, Voucher> {

	@Override
	protected void updateEntity(final VoucherStub stub, final Voucher entity, final RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setExpiryDate(stub.getExpiryDate());
		entity.setIdKey(stub.getKey());
		final Contact contact = callback.updateRelationShip(stub.getContactId(), Contact.class);
		entity.setContact(contact);
		final InvoiceLine redeemedInvoiceLine = callback.updateRelationShip(stub.getRedeemedInvoiceLineId(), InvoiceLine.class);
		entity.setRedeemedInvoiceLine(redeemedInvoiceLine);
		final InvoiceLine invoiceLine = callback.updateRelationShip(stub.getInvoiceLineId(), InvoiceLine.class);
		entity.setInvoiceLine(invoiceLine);
		entity.setModified(stub.getModified());
		final Product product = callback.updateRelationShip(stub.getProductId(), Product.class);
		entity.setProduct(product);
		entity.setType(stub.getType());
	}

}
