package ish.oncourse.webservices.replication.v5.updaters;

import ish.common.types.ExpiryType;
import ish.common.types.TypesUtil;
import ish.math.Money;
import ish.oncourse.model.VoucherProduct;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.v4.updaters.UpdaterException;
import ish.oncourse.webservices.v5.stubs.replication.VoucherProductStub;
import org.apache.commons.lang.StringUtils;

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
		if (StringUtils.trimToNull(stub.getSku()) == null) {
			String message = String.format("Voucher product with angelId = %s and willowid = %s and empty SKU detected!",
				stub.getAngelId(), stub.getWillowId());
			throw new UpdaterException(message);
		}
		entity.setSku(stub.getSku());
		entity.setType(stub.getType());
		entity.setMaxCoursesRedemption(stub.getMaxCoursesRedemption());
		if (stub.getValue() != null) {
			entity.setValue(Money.valueOf(stub.getValue()));
		}
		if (stub.getPriceExTax() != null) {
			entity.setPriceExTax(Money.valueOf(stub.getPriceExTax()));
		}
		if (stub.getTaxAdjustment() != null){
			entity.setTaxAdjustment(Money.valueOf(stub.getTaxAdjustment()));
		}
	}

}
