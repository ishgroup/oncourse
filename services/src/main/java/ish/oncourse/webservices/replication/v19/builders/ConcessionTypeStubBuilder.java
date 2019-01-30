package ish.oncourse.webservices.replication.v19.builders;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v19.stubs.replication.ConcessionTypeStub;

public class ConcessionTypeStubBuilder extends AbstractWillowStubBuilder<ConcessionType, ConcessionTypeStub> {

	@Override
	protected ConcessionTypeStub createFullStub(ConcessionType entity) {
		ConcessionTypeStub stub = new ConcessionTypeStub();
		stub.setCreated(entity.getCreated());
		stub.setCredentialExpiryDays(entity.getCredentialExpiryDays());
		stub.setHasConcessionNumber(entity.getHasConcessionNumber());
		stub.setHasExpiryDate(entity.getHasExpiryDate());
		stub.setIsConcession(entity.getIsConcession());
		stub.setIsEnabled(entity.getIsEnabled());
		stub.setModified(entity.getModified());
		stub.setName(entity.getName());
		stub.setRequiresCredentialCheck(entity.getRequiresCredentialCheck());
		return stub;
	}
}
