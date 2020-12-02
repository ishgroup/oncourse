/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.reference.updater;

import ish.oncourse.server.cayenne.Language;
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
