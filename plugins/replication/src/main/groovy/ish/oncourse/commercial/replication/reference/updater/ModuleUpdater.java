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

import ish.common.types.ModuleType;
import ish.common.types.TypesUtil;
import ish.oncourse.server.cayenne.Module;
import ish.oncourse.server.cayenne.TrainingPackage;
import ish.oncourse.webservices.v7.stubs.reference.ModuleStub;
import org.apache.cayenne.ObjectContext;

import java.util.Date;

/**
 */
public class ModuleUpdater extends BaseReferenceUpdater<ModuleStub> {

	public ModuleUpdater(ObjectContext ctx) {
		super(ctx);
	}

	/**
	 * @see IReferenceUpdater#updateRecord(ish.oncourse.webservices.util.GenericReferenceStub)
	 */
	@Override
	public void updateRecord(ModuleStub record) {
		var module = findOrCreateEntity(Module.class, record.getWillowId());
		module.setIsCustom(false);
		if (record.getCreated() == null) {
			record.setCreated(new Date());
		}
		if (record.getModified() == null) {
			record.setModified(new Date());
		}
		module.setCreatedOn(record.getCreated());

		if (record.getTrainingPackageId() != null) {
			var tp = findEntity(TrainingPackage.class, record.getTrainingPackageId());
			module.setTrainingPackage(tp);
		}

		module.setFieldOfEducation(record.getFieldOfEducation());
		module.setType(TypesUtil.getEnumForDatabaseValue(record.getType(), ModuleType.class));
		module.setNationalCode(record.getNationalCode());
		module.setModifiedOn(record.getModified());
		module.setTitle(record.getTitle());
		module.setWillowId(record.getWillowId());
	}

}
