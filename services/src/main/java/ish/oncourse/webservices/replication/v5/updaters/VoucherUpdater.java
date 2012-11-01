package ish.oncourse.webservices.replication.v5.updaters;

import ish.common.types.PaymentSource;
import ish.common.types.TypesUtil;
import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Voucher;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v5.stubs.replication.VoucherStub;

public class VoucherUpdater extends AbstractProductItemUpdater<VoucherStub, Voucher> {

	@Override
	protected void updateEntity(final VoucherStub stub, final Voucher entity, final RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback);
		
		entity.setExpiryDate(stub.getExpiryDate());
		entity.setIdKey(stub.getKey());
		Contact contact = callback.updateRelationShip(stub.getContactId(), Contact.class);
		entity.setContact(contact);
		entity.setCode(stub.getCode());
		entity.setRedeemedCoursesCount(stub.getRedeemedCoursesCount());
		entity.setRedemptionValue(Money.valueOf(stub.getRedemptionValue()));
		PaymentSource source = TypesUtil.getEnumForDatabaseValue(stub.getSource(), PaymentSource.class);
		entity.setSource(source);
	}

}
