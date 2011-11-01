package ish.oncourse.admin.services.ntis;


import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import ish.oncourse.model.Module;
import ish.oncourse.model.Qualification;
import ish.oncourse.model.TrainingPackage;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.reference.ReferenceService;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.datacontract.schemas._2004._07.system.DateTimeOffset;

import au.gov.training.services.trainingcomponent.Classification;
import au.gov.training.services.trainingcomponent.DeletedSearchRequest;
import au.gov.training.services.trainingcomponent.DeletedTrainingComponent;
import au.gov.training.services.trainingcomponent.ITrainingComponentService;
import au.gov.training.services.trainingcomponent.ObjectFactory;
import au.gov.training.services.trainingcomponent.Release;
import au.gov.training.services.trainingcomponent.ReleaseComponent;
import au.gov.training.services.trainingcomponent.TrainingComponent;
import au.gov.training.services.trainingcomponent.TrainingComponentDetailsRequest;
import au.gov.training.services.trainingcomponent.TrainingComponentInformationRequested;
import au.gov.training.services.trainingcomponent.TrainingComponentModifiedSearchRequest;
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
	
	private Long ishVersion;
	
	/* (non-Javadoc)
	 * @see ish.oncourse.admin.services.ntis.INTISUpdateService#doUpdate()
	 */
	@Override
	public NTISResult doUpdate(Date from, Date to, Class<?> type) throws NTISException {
		
		try {
		
			ishVersion = referenceService.findMaxIshVersion() + 1;
		
			GregorianCalendar cal = new GregorianCalendar();
		
			DateTimeOffset fromDate = new DateTimeOffset();
			DateTimeOffset toDate = new DateTimeOffset();
		
			cal.setTime(from);
			fromDate.setDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));
			cal.setTime(to);
			toDate.setDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));
		
			if (type == Qualification.class) {
				return updateQualifications(fromDate, toDate);
			}
			else if (type == TrainingPackage.class) {
				return updateTrainingPackages(fromDate, toDate);
			}
			else if (type == Module.class) {
				return updateModules(fromDate, toDate);
			}
		
		} catch (DatatypeConfigurationException e) {
			throw new NTISException();
		}
		
		return null;
		
	}
	
	private NTISResult updateQualifications(DateTimeOffset from, DateTimeOffset to) throws NTISException {
		NTISResult result = new NTISResult();
		
		ObjectContext context = cayenneService.newContext();
		
		ObjectFactory objectFactory = new ObjectFactory();
		
		TrainingComponentTypeFilter typeFilter = new TrainingComponentTypeFilter();
		typeFilter.setIncludeQualification(true);
		typeFilter.setIncludeAccreditedCourse(true);
		
		// search for modified components
		TrainingComponentModifiedSearchRequest request = new TrainingComponentModifiedSearchRequest();
		
		request.setStartDate(objectFactory.createTrainingComponentModifiedSearchRequestStartDate(from));
		request.setEndDate(objectFactory.createTrainingComponentModifiedSearchRequestEndDate(to));
		request.setTrainingComponentTypes(objectFactory.createTrainingComponentTypeFilter(typeFilter));
		
		// deleted search request
		DeletedSearchRequest deletedRequest = new DeletedSearchRequest();
		
		deletedRequest.setStartDate(objectFactory.createTrainingComponentModifiedSearchRequestStartDate(from));
		deletedRequest.setEndDate(objectFactory.createTrainingComponentModifiedSearchRequestEndDate(to));
		
		TrainingComponentInformationRequested info = new TrainingComponentInformationRequested();
		info.setShowClassifications(true);
		
		TrainingComponentDetailsRequest detailsRequest = new TrainingComponentDetailsRequest();
		detailsRequest.setInformationRequest(objectFactory.createTrainingComponentInformationRequested(info));
		
		try {	
						
			TrainingComponentSearchResult searchResult = trainingService.searchByModifiedDate(request);
			
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
				
				detailsRequest.setCode(summary.getCode().getValue());
				TrainingComponent component = trainingService.getDetails(detailsRequest);
				List<Classification> classifications = component.getClassifications().getValue().getClassification();
				for (Classification c : classifications) {
					if ("01".equals(c.getSchemeCode())) {
						q.setAnzsco(c.getValueCode());
					}
					else if ("02".equals(c.getSchemeCode())) {
						q.setAsco(c.getValueCode());
					}
					else if ("04".equals(c.getSchemeCode())) {
						q.setFieldOfEducation(c.getValueCode());
					}
					else if ("05".equals(c.getSchemeCode())) {
						q.setLevel(c.getValueCode());
					}
				}
			}
			
			// remove deleted training packages from db
			List<DeletedTrainingComponent> deletedComponents = 
					trainingService.searchDeletedByDeletedDate(deletedRequest).getDeletedTrainingComponent();
		
			for (DeletedTrainingComponent c : deletedComponents) {
				SelectQuery query = new SelectQuery(Qualification.class);
				Expression exp = ExpressionFactory.matchExp("nationalCode", c.getNationalCode().getValue());
				query.setQualifier(exp);
				
				List<Qualification> r = context.performQuery(query);
				if (!r.isEmpty()) {
					context.deleteObject(r.get(0));
				}
			}
			
			List<Qualification> created = (List<Qualification>) context.newObjects();
			for (Qualification q : created) {
				q.setIshVersion(ishVersion);
			}
			
			List<Qualification> updated = (List<Qualification>) context.modifiedObjects();
			for (Qualification q : updated) {
				q.setIshVersion(ishVersion);
			}
			
			context.commitChanges();
			
			result.setNumberOfNew(created.size());
			result.setNumberOfUpdated(updated.size());
		}
		catch (Exception e) {
			throw new NTISException();
		}
		
		return result;
	}
	
	private NTISResult updateTrainingPackages(DateTimeOffset from, DateTimeOffset to) throws NTISException {
		NTISResult result = new NTISResult();
		
		ObjectContext context = cayenneService.newContext();
		
		ObjectFactory objectFactory = new ObjectFactory();
		
		TrainingComponentTypeFilter typeFilter = new TrainingComponentTypeFilter();
		typeFilter.setIncludeTrainingPackage(true);

		// search for modified components
		TrainingComponentModifiedSearchRequest request = new TrainingComponentModifiedSearchRequest();
		
		request.setStartDate(objectFactory.createTrainingComponentModifiedSearchRequestStartDate(from));
		request.setEndDate(objectFactory.createTrainingComponentModifiedSearchRequestEndDate(to));
		request.setTrainingComponentTypes(objectFactory.createTrainingComponentTypeFilter(typeFilter));
		
		// deleted search request
		DeletedSearchRequest deletedRequest = new DeletedSearchRequest();
		
		deletedRequest.setStartDate(objectFactory.createTrainingComponentModifiedSearchRequestStartDate(from));
		deletedRequest.setEndDate(objectFactory.createTrainingComponentModifiedSearchRequestEndDate(to));
		
		TrainingComponentInformationRequested info = new TrainingComponentInformationRequested();
		info.setShowComponents(true);
		info.setShowReleases(true);
		
		TrainingComponentDetailsRequest detailsRequest = new TrainingComponentDetailsRequest();
		detailsRequest.setInformationRequest(objectFactory.createTrainingComponentInformationRequested(info));
			
		try {
		
			TrainingComponentSearchResult searchResult = trainingService.searchByModifiedDate(request);
			
			for (TrainingComponentSummary summary : searchResult.getResults().getValue().getTrainingComponentSummary()) {
				
				SelectQuery query = new SelectQuery(TrainingPackage.class);
				Expression exp = ExpressionFactory.matchExp("nationalISC", summary.getCode().getValue());
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
				
				TrainingComponent component = trainingService.getDetails(detailsRequest);
				List<Release> releases = component.getReleases().getValue().getRelease();
				List<ReleaseComponent> components = releases.get(0).getComponents().getValue().getReleaseComponent();
				for (ReleaseComponent c : components) {
					String code = c.getCode().getValue();
					String type = c.getType().get(0);
					if ("Qualification".equals(type)) {
						
						SelectQuery q = new SelectQuery(Qualification.class);
						Expression e = ExpressionFactory.matchExp("nationalCode", code);
						q.setQualifier(e);
						
						List<Qualification> qual = context.performQuery(q);
						if (!qual.isEmpty()) {
							qual.get(0).setTrainingPackageId(tp.getId());
						}
					}
					else if ("Unit".equals(type)) {
						SelectQuery q = new SelectQuery(Module.class);
						Expression e = ExpressionFactory.matchExp("nationalCode", code);
						q.setQualifier(e);
						
						List<Module> module = context.performQuery(q);
						if (!module.isEmpty()) {
							module.get(0).setTrainingPackageId(tp.getId());
						}
					}
				}
			}
			
			// remove deleted training packages from db
			List<DeletedTrainingComponent> deletedComponents = 
					trainingService.searchDeletedByDeletedDate(deletedRequest).getDeletedTrainingComponent();
		
			for (DeletedTrainingComponent c : deletedComponents) {
				SelectQuery query = new SelectQuery(TrainingPackage.class);
				Expression exp = ExpressionFactory.matchExp("nationalISC", c.getNationalCode().getValue());
				query.setQualifier(exp);
				
				List<TrainingPackage> r = context.performQuery(query);
				if (!r.isEmpty()) {
					context.deleteObject(r.get(0));
				}
			}

			List<TrainingPackage> created = (List<TrainingPackage>) context.newObjects();
			for (TrainingPackage p : created) {
				p.setIshVersion(ishVersion);
			}
			
			List<TrainingPackage> updated = (List<TrainingPackage>) context.modifiedObjects();
			for (TrainingPackage p : updated) {
				p.setIshVersion(ishVersion);
			}
			
			context.commitChanges();
			
			result.setNumberOfNew(created.size());
			result.setNumberOfUpdated(updated.size());
		}
		catch (Exception e) {
			throw new NTISException();
		}
		
		return result;
	}
	
	private NTISResult updateModules(DateTimeOffset from, DateTimeOffset to) throws NTISException {
		
		NTISResult result = new NTISResult();
		
		ObjectContext context = cayenneService.newContext();
		
		ObjectFactory objectFactory = new ObjectFactory();
		
		TrainingComponentTypeFilter typeFilter = new TrainingComponentTypeFilter();
		typeFilter.setIncludeUnit(true);
		typeFilter.setIncludeAccreditedCourseModule(true);

		// search for modified components
		TrainingComponentModifiedSearchRequest request = new TrainingComponentModifiedSearchRequest();
		
		request.setStartDate(objectFactory.createTrainingComponentModifiedSearchRequestStartDate(from));
		request.setEndDate(objectFactory.createTrainingComponentModifiedSearchRequestEndDate(to));
		request.setTrainingComponentTypes(objectFactory.createTrainingComponentTypeFilter(typeFilter));
		
		// deleted search request
		DeletedSearchRequest deletedRequest = new DeletedSearchRequest();
		
		deletedRequest.setStartDate(objectFactory.createTrainingComponentModifiedSearchRequestStartDate(from));
		deletedRequest.setEndDate(objectFactory.createTrainingComponentModifiedSearchRequestEndDate(to));
		
		TrainingComponentInformationRequested info = new TrainingComponentInformationRequested();
		info.setShowClassifications(true);
		
		TrainingComponentDetailsRequest detailsRequest = new TrainingComponentDetailsRequest();
		detailsRequest.setInformationRequest(objectFactory.createTrainingComponentInformationRequested(info));
			
		try {
		
			TrainingComponentSearchResult searchResult = trainingService.searchByModifiedDate(request);
			
			for (TrainingComponentSummary summary : searchResult.getResults().getValue().getTrainingComponentSummary()) {
				
				SelectQuery query = new SelectQuery(Module.class);
				Expression exp = ExpressionFactory.matchExp("nationalCode", summary.getCode().getValue());
				query.setQualifier(exp);
				
				Module m;
				List<Module> r = context.performQuery(query);
				if (r.isEmpty()) {
					m = context.newObject(Module.class);
				}
				else {
					m = r.get(0);
				}

				m.setNationalCode(summary.getCode().getValue());
				m.setTitle(summary.getTitle().getValue());
				m.setCreated(summary.getCreatedDate().getDateTime().toGregorianCalendar().getTime());
				m.setModified(summary.getUpdatedDate().getDateTime().toGregorianCalendar().getTime());
				
				detailsRequest.setCode(summary.getCode().getValue());
				TrainingComponent component = trainingService.getDetails(detailsRequest);
				List<Classification> classifications = component.getClassifications().getValue().getClassification();
				for (Classification c : classifications) {
					if ("04".equals(c.getSchemeCode())) {
						m.setFieldOfEducation(c.getValueCode());
					}
				}
			}
			
			// remove deleted training packages from db
			List<DeletedTrainingComponent> deletedComponents = 
					trainingService.searchDeletedByDeletedDate(deletedRequest).getDeletedTrainingComponent();
		
			for (DeletedTrainingComponent c : deletedComponents) {
				SelectQuery query = new SelectQuery(Module.class);
				Expression exp = ExpressionFactory.matchExp("nationalCode", c.getNationalCode().getValue());
				query.setQualifier(exp);
				
				List<Module> r = context.performQuery(query);
				if (!r.isEmpty()) {
					context.deleteObject(r.get(0));
				}
			}

			List<Module> created = (List<Module>) context.newObjects();
			for (Module m : created) {
				m.setIshVersion(ishVersion);
			}
			
			List<Module> updated = (List<Module>) context.modifiedObjects();
			for (Module m : updated) {
				m.setIshVersion(ishVersion);
			}
			
			context.commitChanges();
			
			result.setNumberOfNew(created.size());
			result.setNumberOfUpdated(updated.size());
		}
		catch (Exception e) {
			throw new NTISException();
		}
		
		return result;
	}
	
}
