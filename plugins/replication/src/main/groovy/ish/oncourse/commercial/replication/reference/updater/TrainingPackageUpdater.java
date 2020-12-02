/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.reference.updater;

import ish.oncourse.server.cayenne.TrainingPackage;
import ish.oncourse.webservices.v7.stubs.reference.TrainingPackageStub;
import org.apache.cayenne.ObjectContext;

/**
 */
public class TrainingPackageUpdater extends BaseReferenceUpdater<TrainingPackageStub> {

	public TrainingPackageUpdater(ObjectContext ctx) {
		super(ctx);
	}

	/**
	 * @see IReferenceUpdater#updateRecord(ish.oncourse.webservices.util.GenericReferenceStub)
	 */
	@Override
	public void updateRecord(TrainingPackageStub record) {
		var p = findOrCreateEntity(TrainingPackage.class, record.getWillowId());

		p.setCopyrightCategory(record.getCopyrightCategory());
		p.setCopyrightContact(record.getCopyrightContract());
		p.setCreatedOn(record.getCreated());
		p.setDeveloper(record.getDeveloper());
		p.setEndorsementFrom(record.getEndorsementFrom());
		p.setEndorsementTo(record.getEndorsementTo());
		p.setModifiedOn(record.getModified());
		p.setNationalISC(record.getNationalISC());
		p.setPurchaseFrom(record.getPurchaseFrom());
		p.setTitle(record.getTitle());
		p.setType(record.getType());
		p.setWillowId(record.getWillowId());
	}
}
