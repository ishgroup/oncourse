/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.admin.services.ntis.trainingcomponent;

import au.gov.training.services.trainingcomponent.*;
import ish.common.types.QualificationType;
import ish.oncourse.model.Qualification;
import ish.oncourse.model.TrainingPackage;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.reference.ReferenceService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datacontract.schemas._2004._07.system.DateTimeOffset;

import java.util.List;

public class QualificationNTISUpdater extends AbstractTrainingComponentNTISUpdater {

	private static final Logger logger = LogManager.getLogger();

	public QualificationNTISUpdater(
			ITrainingComponentService trainingService,
			ICayenneService cayenneService,
			ReferenceService referenceService,
			DateTimeOffset from,
			DateTimeOffset to) {
		super(trainingService, cayenneService, referenceService, from, to);
	}

	protected boolean processRecord(ObjectContext context, TrainingComponentSummary summary)
			throws ITrainingComponentServiceGetDetailsValidationFaultFaultFaultMessage{

		boolean isNewRecord = false;

		String type = summary.getComponentType().get(0);

		TrainingComponentInformationRequested info = new TrainingComponentInformationRequested();
		info.setShowClassifications(true);

		TrainingComponentDetailsRequest detailsRequest = new TrainingComponentDetailsRequest();
		detailsRequest.setInformationRequest(objectFactory.createTrainingComponentInformationRequested(info));

		if (QUALIFICATION.equals(type) || ACCREDITED_COURSE.equals(type) || SKILL_SET.equals(type)) {

			Qualification q = ObjectSelect.query(Qualification.class).
					where(Qualification.NATIONAL_CODE.eq(summary.getCode().getValue())).
					selectFirst(context);
			
			if (q == null) {
				q = context.newObject(Qualification.class);
				isNewRecord = true;
			}

			q.setNationalCode(summary.getCode().getValue());
			setQualificationTitleAndLevel(q, summary.getTitle().getValue());

			switch (type) {
				case QUALIFICATION:
					q.setIsAccreditedCourse(QualificationType.QUALIFICATION_TYPE);
					break;
				case ACCREDITED_COURSE:
					q.setIsAccreditedCourse(QualificationType.COURSE_TYPE);
					break;
				case SKILL_SET:
					q.setIsAccreditedCourse(QualificationType.SKILLSET_TYPE);
					break;
				default:
					throw new IllegalStateException(
							String.format("Qualification type %s is not supported.", type));
			}

			detailsRequest.setCode(summary.getCode().getValue());
			TrainingComponent component = trainingService.getDetails(detailsRequest);
			List<Classification> classifications = component.getClassifications().getValue().getClassification();
			for (Classification c : classifications) {
				if (ANZSCO_ID.equals(c.getSchemeCode())) {
					q.setAnzsco(c.getValueCode());
				} else if (ASCO_ID.equals(c.getSchemeCode())) {
					q.setAsco(c.getValueCode());
				} else if (QUALIFICATION_FIELD_OF_EDUCATION_ID.equals(c.getSchemeCode())) {
					if (c.getValueCode() != null) {
						q.setFieldOfEducation(c.getValueCode());
					}
				} else if (LEVEL_OF_EDUCATION_ID.equals(c.getSchemeCode())) {
					if (c.getValueCode() != null) {
						q.setLevelCode(c.getValueCode());
					}
				}
			}

			if (component.getParentCode() != null) {
				
				TrainingPackage parent = ObjectSelect.query(TrainingPackage.class).
						where(TrainingPackage.NATIONAL_ISC.eq(component.getParentCode().getValue())).
						selectFirst(context);
						
				if (parent != null) {
					q.setTrainingPackageId(parent.getId());
				}
			}

			if (!QualificationType.SKILLSET_TYPE.equals(q.getIsAccreditedCourse()) && q.getLevelCode() == null) {
				logger.error("Skipping qualification record without level of education code. National code is {}", q.getNationalCode());
				context.deleteObjects(q);
			}
		}

		return isNewRecord;
	}

	public static void setQualificationTitleAndLevel(Qualification q,String title) {
		for (String levelName : LEVEL_NAMES) {
			if (title.startsWith(levelName)) {
				q.setTitle(title.replace(levelName, "").trim());
				q.setLevel(levelName);
				return;
			}
		}
		q.setTitle(title);
		q.setLevel("");
	}

	@Override
	protected void deleteRecord(ObjectContext context, DeletedTrainingComponent component) {
		Qualification q = ObjectSelect.query(Qualification.class).
				where(Qualification.NATIONAL_CODE.eq(component.getNationalCode().getValue())).
				selectOne(context);
		
		if (q != null) {
			context.deleteObjects(q);
		}
	}

	@Override
	protected TrainingComponentTypeFilter getTrainingComponentTypeFilter() {
		TrainingComponentTypeFilter typeFilter = new TrainingComponentTypeFilter();

		typeFilter.setIncludeQualification(true);
		typeFilter.setIncludeAccreditedCourse(true);
		typeFilter.setIncludeSkillSet(true);

		return typeFilter;
	}
}
