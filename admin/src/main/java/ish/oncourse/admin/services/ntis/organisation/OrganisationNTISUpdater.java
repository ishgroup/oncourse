/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.admin.services.ntis.organisation;

import au.gov.training.services.organisation.*;
import ish.oncourse.admin.services.ntis.AbstractComponentNTISUpdater;
import ish.oncourse.admin.services.ntis.NTISException;
import ish.oncourse.admin.services.ntis.NTISResult;
import ish.oncourse.model.Organisation;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.reference.V6ReferenceService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datacontract.schemas._2004._07.system.DateTimeOffset;

import java.util.Date;
import java.util.List;

public class OrganisationNTISUpdater extends AbstractComponentNTISUpdater {

	private static final Logger logger = LogManager.getLogger();

	protected IOrganisationService organisationService;

	protected ObjectFactory objectFactory;

	public OrganisationNTISUpdater(
			IOrganisationService organisationService,
			ICayenneService cayenneService,
			V6ReferenceService referenceService,
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

			totalModified += saveChanges(totalNew, context);

			NTISResult result = new NTISResult();
			result.setNumberOfNew(totalNew);
			result.setNumberOfUpdated(totalModified);
			return result;

		} catch (Exception e) {
			logger.info("NTIS Qualifications update failed with exception.", e);
			throw new NTISException(e);
		}
	}

	protected boolean processRecord(ObjectContext context, OrganisationSearchResultItem item)
			throws IOrganisationServiceGetDetailsValidationFaultFaultFaultMessage {

		boolean isNewRecord = false;

		Organisation o = ObjectSelect.query(Organisation.class).
				where(Organisation.CODE.eq(item.getCode().getValue())).
				selectOne(context);
		
		if (o == null) {
			o = context.newObject(Organisation.class);
			isNewRecord = true;
		}

		o.setCode(item.getCode().getValue());
		o.setHasActiveRegistration(item.isHasActiveRegistration());

		fillRecordDetails(o);

		return isNewRecord;
	}

	private void fillRecordDetails(Organisation organisation) throws IOrganisationServiceGetDetailsValidationFaultFaultFaultMessage {

		OrganisationInformationRequested info = new OrganisationInformationRequested();

		info.setShowResponsibleLegalPersons(true);
		info.setShowTradingNames(true);
		info.setShowRegistrationPeriods(true);
		info.setShowUrls(true);

		OrganisationDetailsRequest detailsRequest = new OrganisationDetailsRequest();
		detailsRequest.setInformationRequested(objectFactory.createOrganisationInformationRequested(info));
		detailsRequest.setCode(organisation.getCode());

		au.gov.training.services.organisation.Organisation organisationDetails = organisationService.getDetails(detailsRequest);

		if (!organisationDetails.getResponsibleLegalPersons().getValue().getResponsibleLegalPerson().isEmpty()) {

			ResponsibleLegalPerson legalPerson = organisationDetails.getResponsibleLegalPersons().getValue().getResponsibleLegalPerson().get(0);

			String abn = !legalPerson.getAbns().getValue().getString().isEmpty() ?
					legalPerson.getAbns().getValue().getString().get(0) : null;

			Date registrationStart = legalPerson.getStartDate() != null ?
					legalPerson.getStartDate().getValue().toGregorianCalendar().getTime() : null;
			Date registrationEnd = legalPerson.getEndDate() != null ?
					legalPerson.getEndDate().getValue().toGregorianCalendar().getTime() : null;

			organisation.setLegalPersonName(legalPerson.getName());
			organisation.setAbn(abn);
			organisation.setRegistrationStart(registrationStart);
			organisation.setRegistrationEnd(registrationEnd);
		}

		String tradingName = !organisationDetails.getTradingNames().getValue().getTradingName().isEmpty() ?
				organisationDetails.getTradingNames().getValue().getTradingName().get(0).getName().getValue() : null;

		organisation.setTradingName(tradingName);

		String webAddress = !organisationDetails.getUrls().getValue().getUrl().isEmpty() ?
				organisationDetails.getUrls().getValue().getUrl().get(0).getLink() : null;

		organisation.setWebAddress(webAddress);
	}
}
