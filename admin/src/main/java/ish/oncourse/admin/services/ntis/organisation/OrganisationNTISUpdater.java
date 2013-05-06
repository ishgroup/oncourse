/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.admin.services.ntis.organisation;

import au.gov.training.services.organisation.*;
import au.gov.training.services.organisation.ObjectFactory;
import ish.oncourse.admin.services.ntis.AbstractComponentNTISUpdater;
import ish.oncourse.admin.services.ntis.NTISException;
import ish.oncourse.admin.services.ntis.NTISResult;
import ish.oncourse.model.Organisation;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.reference.ReferenceService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.datacontract.schemas._2004._07.system.DateTimeOffset;

import java.util.List;

public class OrganisationNTISUpdater extends AbstractComponentNTISUpdater {

	private static final Logger LOGGER = Logger.getLogger(OrganisationNTISUpdater.class);

	protected IOrganisationService organisationService;

	protected ObjectFactory objectFactory;

	public OrganisationNTISUpdater(
			IOrganisationService organisationService,
			ICayenneService cayenneService,
			ReferenceService referenceService,
			DateTimeOffset from,
			DateTimeOffset to) {
		super(cayenneService, referenceService, from, to);

		this.organisationService = organisationService;
		this.objectFactory = new ObjectFactory();
	}

	@Override
	public NTISResult doUpdate() throws NTISException {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		// search for modified components
		OrganisationModifiedSearchRequest request = new OrganisationModifiedSearchRequest();

		request.setStartDate(objectFactory.createTrainingComponentModifiedSearchRequestStartDate(from));
		request.setEndDate(objectFactory.createTrainingComponentModifiedSearchRequestEndDate(to));
		request.setPageSize(RESULTS_PAGE_SIZE);

		// deleted search request
		DeletedSearchRequest deletedRequest = new DeletedSearchRequest();

		deletedRequest.setStartDate(objectFactory.createOrganisationModifiedSearchRequestStartDate(from));
		deletedRequest.setEndDate(objectFactory.createOrganisationModifiedSearchRequestEndDate(to));

		try {

			int pageNumber = 1;
			int pageCount;

			do {
				request.setPageNumber(pageNumber);
				OrganisationSearchResult searchResult = organisationService.searchByModifiedDate(request);

				pageCount = (int) Math.ceil((double) searchResult.getCount() / RESULTS_PAGE_SIZE);

				int numberOfNew = 0;

				for (OrganisationSearchResultItem item : searchResult.getResults().getValue().getOrganisationSearchResultItem()) {

					if (processRecord(context, item)) {
						numberOfNew++;
					}
				}

				totalModified += saveChanges(numberOfNew, context);
				totalNew += numberOfNew;
				pageNumber++;
			} while (pageNumber <= pageCount);

			// remove deleted training packages from db
			List<DeletedOrganisation> deletedComponents = organisationService.searchDeletedByDeletedDate(deletedRequest)
					.getDeletedOrganisation();

			for (DeletedOrganisation organisation : deletedComponents) {
				deleteRecord(context, organisation);
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

	protected boolean processRecord(ObjectContext context, OrganisationSearchResultItem item) {

		boolean isNewRecord = false;

		SelectQuery query = new SelectQuery(ish.oncourse.model.Organisation.class);
		Expression exp = ExpressionFactory.matchExp(Organisation.CODE_PROPERTY, item.getCode().getValue());
		query.setQualifier(exp);

		Organisation organisation = (Organisation) Cayenne.objectForQuery(context, query);

		if (organisation == null) {
			organisation = context.newObject(Organisation.class);
			isNewRecord = true;
		}

		organisation.setCode(item.getCode().getValue());
		organisation.setTradingName(item.getTradingName().getValue());
		organisation.setLegalPersonName(item.getLegalPersonName().getValue());
		organisation.setHasActiveRegistration(item.isHasActiveRegistration());

		return isNewRecord;
	}

	protected void deleteRecord(ObjectContext context, DeletedOrganisation organisation) {
		SelectQuery query = new SelectQuery(Organisation.class);
		Expression exp = ExpressionFactory.matchExp(Organisation.CODE_PROPERTY, organisation.getOrganisationCode().getValue());
		query.setQualifier(exp);

		Organisation o = (Organisation) Cayenne.objectForQuery(context, query);

		if (o != null) {
			context.deleteObjects(o);
		}
	}
}
