package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.webservices.v4.stubs.replication.ConcessionTypeStub;

public class ConcessionTypeUpdater extends AbstractWillowUpdater<ConcessionTypeStub, ConcessionType> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater#
	 * updateEntity
	 * (ish.oncourse.webservices.v4.stubs.replication.ReplicationStub,
	 * ish.oncourse.model.Queueable,
	 * ish.oncourse.webservices.replication.updaters.RelationShipCallback)
	 */
	@Override
	protected void updateEntity(ConcessionTypeStub s, ConcessionType e, RelationShipCallback callback) {
		e.setCreated(s.getCreated());
		e.setCredentialExpiryDays(s.getCredentialExpiryDays());
		e.setHasConcessionNumber(s.isHasConcessionNumber());
		e.setHasExpiryDate(s.isHasExpiryDate());
		e.setIsConcession(s.isIsConcession());
		e.setIsEnabled(s.isIsEnabled());
		e.setModified(s.getModified());
		e.setName(s.getName());
		e.setRequiresCredentialCheck(s.isRequiresCredentialCheck());
	}
}
