/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.reference.updater;


import ish.oncourse.webservices.util.GenericReferenceStub;
import ish.oncourse.webservices.v7.stubs.reference.*;
import org.apache.cayenne.ObjectContext;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class ReferenceUpdaterFactory implements IReferenceUpdaterFactory {

	@Override
	public IReferenceUpdater<GenericReferenceStub> newReferenceUpdater(ObjectContext ctx) {
		return new ReferenceUpdaterImpl(ctx);
	}

	@SuppressWarnings("all")
	private static class ReferenceUpdaterImpl implements IReferenceUpdater<GenericReferenceStub> {

		Map<Class<?>, IReferenceUpdater> map = new HashMap<Class<?>, IReferenceUpdater>();

		private ReferenceUpdaterImpl(ObjectContext ctx) {
			this.map.put(CountryStub.class, new CountryUpdater(ctx));
			this.map.put(LanguageStub.class, new LanguageUpdater(ctx));
			this.map.put(ModuleStub.class, new ModuleUpdater(ctx));
			this.map.put(QualificationStub.class, new QualificationUpdater(ctx));
			this.map.put(TrainingPackageStub.class, new TrainingPackageUpdater(ctx));
			this.map.put(PostcodeStub.class, new PostcodeUpdater(ctx));
		}

		@Override
		public void updateRecord(GenericReferenceStub record) {
			this.map.get(record.getClass()).updateRecord(record);
		}
	}
}
