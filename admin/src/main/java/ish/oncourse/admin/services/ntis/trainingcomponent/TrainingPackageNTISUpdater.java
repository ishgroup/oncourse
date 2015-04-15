/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.admin.services.ntis.trainingcomponent;

import au.gov.training.services.trainingcomponent.DeletedTrainingComponent;
import au.gov.training.services.trainingcomponent.ITrainingComponentService;
import au.gov.training.services.trainingcomponent.TrainingComponentSummary;
import au.gov.training.services.trainingcomponent.TrainingComponentTypeFilter;
import ish.oncourse.model.TrainingPackage;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.reference.ReferenceService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.datacontract.schemas._2004._07.system.DateTimeOffset;

public class TrainingPackageNTISUpdater extends AbstractTrainingComponentNTISUpdater {

	public TrainingPackageNTISUpdater(
			ITrainingComponentService trainingService,
			ICayenneService cayenneService,
			ReferenceService referenceService,
			DateTimeOffset from,
			DateTimeOffset to) {
		super(trainingService, cayenneService, referenceService, from, to);
	}

	@Override
	protected TrainingComponentTypeFilter getTrainingComponentTypeFilter() {
		TrainingComponentTypeFilter typeFilter = new TrainingComponentTypeFilter();

		typeFilter.setIncludeTrainingPackage(true);

		return typeFilter;
	}

	@Override
	protected boolean processRecord(ObjectContext context, TrainingComponentSummary summary) throws Exception {
		boolean isNewRecord = false;

		String type = summary.getComponentType().get(0);

		if (TRAINING_PACKAGE.equals(type)) {
			TrainingPackage tp = ObjectSelect.query(TrainingPackage.class).
					where(TrainingPackage.NATIONAL_ISC.eq(summary.getCode().getValue())).
					selectOne(context);
			if (tp == null) {
				tp = context.newObject(TrainingPackage.class);
				isNewRecord = true;
			}

			tp.setNationalISC(summary.getCode().getValue());
			tp.setTitle(summary.getTitle().getValue());
			tp.setType(summary.getComponentType().get(0));
		}

		return isNewRecord;
	}

	@Override
	protected void deleteRecord(ObjectContext context, DeletedTrainingComponent component) {
		TrainingPackage trainingPackage = ObjectSelect.query(TrainingPackage.class).
				where(TrainingPackage.NATIONAL_ISC.eq(component.getNationalCode().getValue())).
				selectOne(context);
		
		if (trainingPackage != null) {
			context.deleteObjects(trainingPackage);
		}
	}
}
