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

import ish.oncourse.server.cayenne.Language;
import ish.oncourse.server.reference.updater.BaseReferenceUpdater;
import ish.oncourse.server.reference.updater.IReferenceUpdater;
import ish.oncourse.webservices.v7.stubs.reference.LanguageStub;
import org.apache.cayenne.ObjectContext;

/**
 */
public class LanguageUpdater extends BaseReferenceUpdater<LanguageStub> {

	public LanguageUpdater(ObjectContext ctx) {
		super(ctx);
	}

	/**
	 * @see IReferenceUpdater#updateRecord(ish.oncourse.webservices.util.GenericReferenceStub)
	 */
	@Override
	public void updateRecord(LanguageStub record) {
		var lang = findOrCreateEntity(Language.class, record.getWillowId());
		lang.setAbsCode(record.getAbsCode());
		lang.setCreatedOn(record.getCreated());
		lang.setIsActive(record.isIsActive());
		lang.setModifiedOn(record.getModified());
		lang.setName(record.getName());
		lang.setWillowId(record.getWillowId());
	}
}
