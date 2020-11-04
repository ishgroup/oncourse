/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
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
