/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.common.types.ExpiryType
import ish.common.types.TypesUtil
import ish.math.Money
import ish.oncourse.server.cayenne.FieldConfigurationScheme
import ish.oncourse.server.cayenne.VoucherProduct
import ish.oncourse.webservices.v23.stubs.replication.VoucherProductStub

class VoucherProductUpdater extends AbstractProductUpdater<VoucherProductStub, VoucherProduct> {

	@Override
	protected void updateEntity(VoucherProductStub stub, VoucherProduct entity, RelationShipCallback callback) {
		super.updateEntity(stub, entity, callback)

		entity.setExpiryDays(stub.getExpiryDays())
		entity.setMaxCoursesRedemption(stub.getMaxCoursesRedemption())
		if (stub.getExpiryType() != null) {
			entity.setExpiryType(TypesUtil.getEnumForDatabaseValue(stub.getExpiryType(), ExpiryType.class))
		}
		if (stub.getValue() != null) {
			entity.setValue(Money.valueOf(stub.getValue()))
		}
	}
}
