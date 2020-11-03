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

import ish.common.types.QualificationType;
import ish.common.types.TypesUtil;
import ish.oncourse.server.cayenne.Qualification;
import ish.oncourse.server.cayenne.TrainingPackage;
import ish.oncourse.server.reference.updater.BaseReferenceUpdater;
import ish.oncourse.server.reference.updater.IReferenceUpdater;
import ish.oncourse.webservices.v7.stubs.reference.QualificationStub;
import ish.util.LocalDateUtils;
import org.apache.cayenne.ObjectContext;

import java.math.BigDecimal;
import java.util.Date;

/**
 */
public class QualificationUpdater extends BaseReferenceUpdater<QualificationStub> {

	public QualificationUpdater(ObjectContext ctx) {
		super(ctx);
	}

	/**
	 * @see IReferenceUpdater#updateRecord(ish.oncourse.webservices.util.GenericReferenceStub)
	 */
	@Override
	public void updateRecord(QualificationStub record) {
		var q = findOrCreateEntity(Qualification.class, record.getWillowId());
		q.setIsCustom(false);
		q.setAnzsco(record.getAnzsco());
		if (record.getCreated() == null) {
			record.setCreated(new Date());
		}
		if (record.getModified() == null) {
			record.setModified(new Date());
		}
		q.setCreatedOn(record.getCreated());
		q.setFieldOfEducation(record.getFieldOfEducation());

		// the old accreditedCourse column has been hacked into representing an enum since we have lots of possible choices
		q.setType(TypesUtil.getEnumForDatabaseValue(record.getIsAccreditedCourse(), QualificationType.class));

		if (record.getTrainingPackageId() != null) {
			var tp = findEntity(TrainingPackage.class, record.getTrainingPackageId());
			q.setTrainingPackage(tp);
		}

		q.setWillowId(record.getWillowId());
		q.setTitle(record.getTitle());
		q.setLevel(record.getLevel());
		if (q.getIsOffered() == null) {
			q.setIsOffered(false);
		}
		q.setNationalCode(record.getNationalCode());
		q.setNewApprenticeship(record.getNewApprentices());

		var nominalHours = record.getNominalHours();

		if (nominalHours != null && q.getNominalHours() == null) {
			q.setNominalHours(new BigDecimal(nominalHours));
		}

		q.setReviewDate(LocalDateUtils.dateToValue(record.getReviewDate()));
	}
}
