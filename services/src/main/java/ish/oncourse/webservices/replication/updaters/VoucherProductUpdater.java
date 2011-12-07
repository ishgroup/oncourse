package ish.oncourse.webservices.replication.updaters;

import ish.common.types.ExpiryType;
import ish.common.types.TypesUtil;
import ish.oncourse.model.VoucherProduct;
import ish.oncourse.webservices.v4.stubs.replication.VoucherProductStub;

public class VoucherProductUpdater extends AbstractWillowUpdater<VoucherProductStub, VoucherProduct> {

	@Override
	protected void updateEntity(final VoucherProductStub stub, final VoucherProduct entity, final RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setDescription(stub.getDescription());
		entity.setExpiryDays(stub.getExpiryDays());
		final Integer expiryType = stub.getExpiryType();
		if (expiryType != null) {
			entity.setExpiryType(TypesUtil.getEnumForDatabaseValue(expiryType, ExpiryType.class));
		}
		entity.setIsOnSale(stub.isIsOnSale());
		entity.setIsWebVisible(stub.isIsWebVisible());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setNotes(stub.getNotes());
		entity.setSku(stub.getSku());
		entity.setType(stub.getType());
	}

}
