package ish.oncourse.webservices.replication.v4.updaters;

import ish.common.types.ProductStatus;
import ish.common.types.TypesUtil;
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
		Contact contact = callback.updateRelationShip(stub.getContactId(), Contact.class);
		entity.setContact(contact);
		InvoiceLine invoiceLine = callback.updateRelationShip(stub.getInvoiceLineId(), InvoiceLine.class);
		entity.setInvoiceLine(invoiceLine);
		entity.setModified(stub.getModified());
		Product product = callback.updateRelationShip(stub.getProductId(), Product.class);
		entity.setProduct(product);
		entity.setType(stub.getType());
		//set default new status to have ability succeed them
		entity.setStatus(ProductStatus.NEW);
	}

}
