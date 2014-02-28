package ish.oncourse.webservices.replication.v6.updaters;

import ish.common.types.PaymentSource;
import ish.common.types.TypesUtil;
import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherProduct;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v6.stubs.replication.VoucherStub;

public class VoucherUpdater extends AbstractProductItemUpdater<VoucherStub, Voucher> {

	@Override
	protected void updateEntity(final VoucherStub stub, final Voucher entity, final RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback);
		entity.setProduct(callback.updateRelationShip(stub.getProductId(), VoucherProduct.class));
		entity.setExpiryDate(stub.getExpiryDate());
		entity.setIdKey(stub.getKey());
		entity.setContact(callback.updateRelationShip(stub.getContactId(), Contact.class));
		entity.setCode(stub.getCode());
		entity.setRedeemedCoursesCount(stub.getRedeemedCoursesCount());
		entity.setRedemptionValue(stub.getRedemptionValue() != null ? Money.valueOf(stub.getRedemptionValue()) : null);
		entity.setValueOnPurchase(stub.getValueOnPurchase() != null ? Money.valueOf(stub.getValueOnPurchase()) : null);
		entity.setSource(TypesUtil.getEnumForDatabaseValue(stub.getSource(), PaymentSource.class));
	}

}
