package ish.oncourse.webservices.replication.v7.updaters;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v7.stubs.replication.ConcessionTypeStub;

public class ConcessionTypeUpdater extends AbstractWillowUpdater<ConcessionTypeStub, ConcessionType> {

	@Override
	protected void updateEntity(ConcessionTypeStub stub, ConcessionType entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setCredentialExpiryDays(stub.getCredentialExpiryDays());
		entity.setHasConcessionNumber(stub.isHasConcessionNumber());
		entity.setHasExpiryDate(stub.isHasExpiryDate());
		entity.setIsConcession(stub.isIsConcession());
		entity.setIsEnabled(stub.isIsEnabled());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setRequiresCredentialCheck(stub.isRequiresCredentialCheck());
	}
}
