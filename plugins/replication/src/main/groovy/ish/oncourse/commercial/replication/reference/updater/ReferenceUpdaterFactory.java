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

import ish.oncourse.server.reference.updater.CountryUpdater;
import ish.oncourse.server.reference.updater.IReferenceUpdater;
import ish.oncourse.server.reference.updater.IReferenceUpdaterFactory;
import ish.oncourse.server.reference.updater.LanguageUpdater;
import ish.oncourse.server.reference.updater.ModuleUpdater;
import ish.oncourse.server.reference.updater.PostcodeUpdater;
import ish.oncourse.server.reference.updater.QualificationUpdater;
import ish.oncourse.server.reference.updater.TrainingPackageUpdater;
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
