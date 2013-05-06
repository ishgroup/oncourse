/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.admin.services.ntis.trainingcomponent;

import au.gov.training.services.trainingcomponent.*;
import ish.oncourse.model.Module;
import ish.oncourse.model.TrainingPackage;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.reference.ReferenceService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.datacontract.schemas._2004._07.system.DateTimeOffset;

import java.util.List;

public class ModuleNTISUpdater extends AbstractTrainingComponentNTISUpdater {

	public ModuleNTISUpdater(
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

		typeFilter.setIncludeUnit(true);
		typeFilter.setIncludeAccreditedCourseModule(true);

		return typeFilter;
	}

	@Override
	protected boolean processRecord(ObjectContext context, TrainingComponentSummary summary) throws Exception {

		boolean isNewRecord = false;

		String type = summary.getComponentType().get(0);

		TrainingComponentInformationRequested info = new TrainingComponentInformationRequested();
		info.setShowClassifications(true);

		TrainingComponentDetailsRequest detailsRequest = new TrainingComponentDetailsRequest();
		detailsRequest.setInformationRequest(objectFactory.createTrainingComponentInformationRequested(info));

		if (MODULE.equals(type) || UNIT.equals(type)) {

			SelectQuery query = new SelectQuery(Module.class);
			Expression exp = ExpressionFactory.matchExp("nationalCode", summary.getCode().getValue());
			query.setQualifier(exp);

			Module m = (Module) Cayenne.objectForQuery(context, query);

			if (m == null) {
				m = context.newObject(Module.class);
				isNewRecord = true;
			}

			m.setNationalCode(summary.getCode().getValue());
			m.setTitle(summary.getTitle().getValue());

			if (MODULE.equals(summary.getComponentType().get(0))) {
				m.setIsModule((byte) 1);
			} else {
				m.setIsModule((byte) 0);
			}

			detailsRequest.setCode(summary.getCode().getValue());
			TrainingComponent component = trainingService.getDetails(detailsRequest);
			List<Classification> classifications = component.getClassifications().getValue().getClassification();
			for (Classification c : classifications) {
				if (MODULE_FIELD_OF_EDUCATION_ID.equals(c.getSchemeCode())) {
					m.setFieldOfEducation(c.getValueCode());
				}
			}

			if (component.getParentCode() != null) {
				Expression e = ExpressionFactory.matchExp(TrainingPackage.NATIONAL_ISC_PROPERTY,
						component.getParentCode().getValue());
				SelectQuery queryParent = new SelectQuery(TrainingPackage.class, e);

				TrainingPackage parent = (TrainingPackage) Cayenne.objectForQuery(context, queryParent);
				if (parent != null) {
					m.setTrainingPackageId(parent.getId());
				}
			}
		}

		return isNewRecord;
	}

	@Override
	protected void deleteRecord(ObjectContext context, DeletedTrainingComponent component) {
		SelectQuery query = new SelectQuery(Module.class);
		Expression exp = ExpressionFactory.matchExp("nationalCode", component.getNationalCode().getValue());
		query.setQualifier(exp);

		Module r = (Module) Cayenne.objectForQuery(context, query);

		if (r != null) {
			context.deleteObjects(r);
		}
	}
}
