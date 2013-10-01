package ish.oncourse.webservices.replication.v5.updaters;

import ish.common.types.ExpiryType;
import ish.common.types.TypesUtil;
import ish.math.Money;
import ish.oncourse.model.MembershipProduct;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.v4.updaters.UpdaterException;
import ish.oncourse.webservices.v5.stubs.replication.MembershipProductStub;
import org.apache.commons.lang.StringUtils;

public class MembershipProductUpdater extends AbstractWillowUpdater<MembershipProductStub, MembershipProduct> {

	@Override
	protected void updateEntity(MembershipProductStub stub, MembershipProduct entity, RelationShipCallback callback) {
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
		if (StringUtils.trimToNull(stub.getSku()) == null) {
			String message = String.format("Membership product with angelId = %s and willowid = %s and empty SKU detected!",
				stub.getAngelId(), stub.getWillowId());
			throw new UpdaterException(message);
		}
		entity.setSku(stub.getSku());
		entity.setType(stub.getType());
		if (stub.getPriceExTax() != null) {
			entity.setPriceExTax(new Money(stub.getPriceExTax()));
		}
		if (stub.getTaxAdjustment() != null) {
			entity.setTaxAdjustment(new Money(stub.getTaxAdjustment()));
		}
		entity.setFeeGST(stub.getFeeGST() != null ? Money.valueOf(stub.getFeeGST()) : Money.ZERO);
	}

}
