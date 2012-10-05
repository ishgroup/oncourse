package ish.oncourse.webservices.replication.v5.updaters;

import ish.common.types.PaymentSource;
import ish.common.types.TypesUtil;
import ish.common.types.VoucherStatus;
import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherProduct;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v5.stubs.replication.VoucherStub;

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
		VoucherProduct product = callback.updateRelationShip(stub.getProductId(), VoucherProduct.class);
		entity.setProduct(product);
		entity.setType(stub.getType());
		entity.setCode(stub.getCode());
		entity.setRedeemedCoursesCount(stub.getRedeemedCoursesCount());
		entity.setRedemptionValue(Money.valueOf(stub.getRedemptionValue()));
		if (stub.getStatus() != null) {
			VoucherStatus status = TypesUtil.getEnumForDatabaseValue(stub.getStatus(), VoucherStatus.class);
			entity.setStatus(status);
		}
		PaymentSource source = TypesUtil.getEnumForDatabaseValue(stub.getSource(), PaymentSource.class);
		entity.setSource(source);
	}

}
