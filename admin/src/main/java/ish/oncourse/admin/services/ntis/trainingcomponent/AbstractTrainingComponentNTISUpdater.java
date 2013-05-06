/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.admin.services.ntis.trainingcomponent;

import au.gov.training.services.trainingcomponent.*;
import ish.oncourse.admin.services.ntis.AbstractComponentNTISUpdater;
import ish.oncourse.admin.services.ntis.NTISException;
import ish.oncourse.admin.services.ntis.NTISResult;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.reference.ReferenceService;
import org.apache.cayenne.ObjectContext;
import org.apache.log4j.Logger;
import org.datacontract.schemas._2004._07.system.DateTimeOffset;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractTrainingComponentNTISUpdater extends AbstractComponentNTISUpdater {

	private static final Logger LOGGER = Logger.getLogger(AbstractTrainingComponentNTISUpdater.class);

	protected static final String QUALIFICATION = "Qualification";
	protected static final String TRAINING_PACKAGE = "TrainingPackage";
	protected static final String ACCREDITED_COURSE = "AccreditedCourse";
	protected static final String SKILL_SET = "SkillSet";
	protected static final String MODULE = "AccreditedCourseModule";
	protected static final String UNIT = "Unit";

	protected static final String ANZSCO_ID = "01";
	protected static final String ASCO_ID = "02";
	protected static final String MODULE_FIELD_OF_EDUCATION_ID = "03";
	protected static final String QUALIFICATION_FIELD_OF_EDUCATION_ID = "04";
	protected static final String LEVEL_OF_EDUCATION_ID = "05";

	protected static final List<String> LEVEL_NAMES = Arrays.asList(
			"Graduate Diploma of",
			"Graduate Diploma (professional specialist) of",
			"Graduate Certificate in",
			"Graduate Certificate (professional specialist) in",
			"Bachelor degree (honours)",
			"Bachelor degree",
			"Advanced Diploma of",
			"Associate Degree",
			"Diploma of",
			"Certificate IV in",
			"Certificate III in",
			"Certificate II in",
			"Certificate I in",
			"Year 12",
			"Year 11",
			"Year 10",
			"Course in",
			"Vocational Graduate Diploma of",
			"Vocational Graduate Certificate in"
	);


	protected ITrainingComponentService trainingService;

	protected ObjectFactory objectFactory;

	public AbstractTrainingComponentNTISUpdater(
			ITrainingComponentService trainingService,
			ICayenneService cayenneService,
			ReferenceService referenceService,
			DateTimeOffset from,
			DateTimeOffset to) {
		super(cayenneService, referenceService, from, to);

		this.trainingService = trainingService;
		this.objectFactory = new ObjectFactory();
	}

	@Override
	public NTISResult doUpdate() throws NTISException {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		TrainingComponentTypeFilter typeFilter = getTrainingComponentTypeFilter();

		// search for modified components
		TrainingComponentModifiedSearchRequest request = new TrainingComponentModifiedSearchRequest();

		request.setStartDate(objectFactory.createTrainingComponentModifiedSearchRequestStartDate(from));
		request.setEndDate(objectFactory.createTrainingComponentModifiedSearchRequestEndDate(to));
		request.setTrainingComponentTypes(objectFactory.createTrainingComponentTypeFilter(typeFilter));
		request.setPageSize(RESULTS_PAGE_SIZE);

		// deleted search request
		DeletedSearchRequest deletedRequest = new DeletedSearchRequest();

		deletedRequest.setStartDate(objectFactory.createTrainingComponentModifiedSearchRequestStartDate(from));
		deletedRequest.setEndDate(objectFactory.createTrainingComponentModifiedSearchRequestEndDate(to));

		try {

			int pageNumber = 1;
			int pageCount;

			do {
				request.setPageNumber(pageNumber);
				TrainingComponentSearchResult searchResult = trainingService.searchByModifiedDate(request);

				pageCount = (int) Math.ceil((double) searchResult.getCount() / RESULTS_PAGE_SIZE);

				int numberOfNew = 0;

				for (TrainingComponentSummary summary : searchResult.getResults().getValue().getTrainingComponentSummary()) {

					if (processRecord(context, summary)) {
						numberOfNew++;
					}
				}

				totalModified += saveChanges(numberOfNew, context);
				totalNew += numberOfNew;
				pageNumber++;
			} while (pageNumber <= pageCount);

			// remove deleted training packages from db
			List<DeletedTrainingComponent> deletedComponents = trainingService.searchDeletedByDeletedDate(deletedRequest)
					.getDeletedTrainingComponent();

			for (DeletedTrainingComponent c : deletedComponents) {
				deleteRecord(context, c);
			}

			totalModified += saveChanges(totalNew, context);

			NTISResult result = new NTISResult();
			result.setNumberOfNew(totalNew);
			result.setNumberOfUpdated(totalModified);
			return result;

		} catch (Exception e) {
			LOGGER.info("NTIS Qualifications update failed with exception.", e);
			throw new NTISException(e);
		}

	}

	protected abstract boolean processRecord(ObjectContext context, TrainingComponentSummary summary) throws Exception;

	protected abstract void deleteRecord(ObjectContext context, DeletedTrainingComponent component);

	protected abstract TrainingComponentTypeFilter getTrainingComponentTypeFilter();
}
