package ish.oncourse.admin.services.ntis;


import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import ish.oncourse.model.Qualification;
import ish.oncourse.model.TrainingPackage;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.reference.ReferenceService;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

import au.gov.training.services.trainingcomponent.ITrainingComponentService;
import au.gov.training.services.trainingcomponent.TrainingComponentDetailsRequest;
import au.gov.training.services.trainingcomponent.TrainingComponentInformationRequested;
import au.gov.training.services.trainingcomponent.TrainingComponentSearchRequest;
import au.gov.training.services.trainingcomponent.TrainingComponentSearchResult;
import au.gov.training.services.trainingcomponent.TrainingComponentSummary;
import au.gov.training.services.trainingcomponent.TrainingComponentTypeFilter;

public class NTISUpdaterImpl implements INTISUpdater {

	@Inject
	private ITrainingComponentService trainingService;
	
	@Inject
	private ReferenceService referenceService;
	
	@Inject
	private ICayenneService cayenneService;
	
	/* (non-Javadoc)
	 * @see ish.oncourse.admin.services.ntis.INTISUpdateService#doUpdate()
	 */
	@Override
	public NTISResult doUpdate(Class<?> type) {
		
		if (type == Qualification.class) {
			return updateQualifications();
		}
		else if (type == TrainingPackage.class) {
			return updateTrainingPackages();
		}
		
		
		
		return null;
		
	}
	
	private NTISResult updateQualifications() {
		
		NTISResult result = new NTISResult();
		
		ObjectContext context = cayenneService.newContext();
		
		TrainingComponentTypeFilter typeFilter = new TrainingComponentTypeFilter();
		
		typeFilter.setIncludeQualification(true);
		typeFilter.setIncludeAccreditedCourse(true);
			
		JAXBElement<TrainingComponentTypeFilter> filters = new JAXBElement<TrainingComponentTypeFilter>(
				new QName("qualification"), TrainingComponentTypeFilter.class, typeFilter);
			
		TrainingComponentSearchRequest request = new TrainingComponentSearchRequest();
		request.setTrainingComponentTypes(filters);
		
		TrainingComponentInformationRequested info = new TrainingComponentInformationRequested();
		info.setShowClassifications(true);
		
		JAXBElement<TrainingComponentInformationRequested> infoRequested = new JAXBElement<TrainingComponentInformationRequested>(
				new QName("classification"), TrainingComponentInformationRequested.class, info);
		
		TrainingComponentDetailsRequest detailsRequest = new TrainingComponentDetailsRequest();
		detailsRequest.setInformationRequest(infoRequested);
		
			
		try {
			TrainingComponentSearchResult searchResult = trainingService.search(request);
			for (TrainingComponentSummary summary : searchResult.getResults().getValue().getTrainingComponentSummary()) {
				
				SelectQuery query = new SelectQuery(Qualification.class);
				Expression exp = ExpressionFactory.matchExp("nationalCode", summary.getCode().getValue());
				query.setQualifier(exp);
				
				Qualification q;
				List<Qualification> r = context.performQuery(query);
				if (r.isEmpty()) {
					q = context.newObject(Qualification.class);
				}
				else {
					q = r.get(0);
				}
				q.setNationalCode(summary.getCode().getValue());
				q.setTitle(summary.getTitle().getValue());
				q.setCreated(summary.getCreatedDate().getDateTime().toGregorianCalendar().getTime());
				q.setModified(summary.getUpdatedDate().getDateTime().toGregorianCalendar().getTime());
			}
		}
		catch (Exception e) {
				
		}
		
		return result;
	}
	
	private NTISResult updateTrainingPackages() {
		NTISResult result = new NTISResult();
		
		ObjectContext context = cayenneService.newContext();
		
		TrainingComponentTypeFilter typeFilter = new TrainingComponentTypeFilter();
		
		typeFilter.setIncludeTrainingPackage(true);
			
		JAXBElement<TrainingComponentTypeFilter> filters = new JAXBElement<TrainingComponentTypeFilter>(
				new QName("trainingPackage"), TrainingComponentTypeFilter.class, typeFilter);
			
		TrainingComponentSearchRequest request = new TrainingComponentSearchRequest();
		request.setTrainingComponentTypes(filters);
			
		try {
			TrainingComponentSearchResult searchResult = trainingService.search(request);
			for (TrainingComponentSummary summary : searchResult.getResults().getValue().getTrainingComponentSummary()) {
				
				SelectQuery query = new SelectQuery(TrainingPackage.class);
				Expression exp = ExpressionFactory.matchExp("nationalIISC", summary.getCode().getValue());
				query.setQualifier(exp);
				
				TrainingPackage tp;
				List<TrainingPackage> r = context.performQuery(query);
				if (r.isEmpty()) {
					tp = context.newObject(TrainingPackage.class);
				}
				else {
					tp = r.get(0);
				}
				tp.setNationalISC(summary.getCode().getValue());
				tp.setTitle(summary.getTitle().getValue());
				tp.setCreated(summary.getCreatedDate().getDateTime().toGregorianCalendar().getTime());
				tp.setModified(summary.getUpdatedDate().getDateTime().toGregorianCalendar().getTime());
				tp.setType(summary.getComponentType().get(0));
				
				
			}
		}
		catch (Exception e) {
				
		}
		
		return result;
	}
	
}
