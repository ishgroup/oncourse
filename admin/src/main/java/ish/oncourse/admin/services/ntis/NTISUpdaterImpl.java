package ish.oncourse.admin.services.ntis;


import ish.oncourse.model.Module;
import ish.oncourse.model.Qualification;
import ish.oncourse.model.TrainingPackage;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.reference.ReferenceService;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
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

	private static final String QUALIFICATION = "Qualification";
	private static final String TRAINING_PACKAGE = "TrainingPackage";
	private static final String ACCREDITED_COURSE = "AccreditedCourse";
	private static final String MODULE = "AccreditedCourseModule";
	private static final String UNIT = "Unit";
	
	private static final String ANZSCO_ID = "01";
	private static final String ASCO_ID = "02";
	private static final String FIELD_OF_EDUCATION_ID = "04";
	private static final String LEVEL_OF_EDUCATION_ID = "05";
	
	private static final Logger LOGGER = Logger.getLogger(NTISUpdaterImpl.class);
	
	@Inject
	private ITrainingComponentService trainingService;
	
	@Inject
	private ReferenceService referenceService;
	
	@Inject
	private ICayenneService cayenneService;
	
	/**
	 * Max number of reference records which causes cayenne commit.
	 */
	private static final int MAX_ENTRIES_TO_COMMIT = 500;
	
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
			
			int entriesToCommit = 0;
			int created = 0;
			int modified = 0;
			
			for (TrainingComponentSummary summary : searchResult.getResults().getValue().getTrainingComponentSummary()) {
				
				String type = summary.getComponentType().get(0);
				
				if (QUALIFICATION.equals(type) || ACCREDITED_COURSE.equals(type)) {
				
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
					
					entriesToCommit += 1;
				
					q.setNationalCode(summary.getCode().getValue());
					q.setTitle(summary.getTitle().getValue());
					q.setCreated(summary.getCreatedDate().getDateTime().toGregorianCalendar().getTime());
					q.setModified(summary.getUpdatedDate().getDateTime().toGregorianCalendar().getTime());
					q.setIshVersion(ishVersion);
				
					detailsRequest.setCode(summary.getCode().getValue());
					TrainingComponent component = trainingService.getDetails(detailsRequest);
					List<Classification> classifications = component.getClassifications().getValue().getClassification();
					for (Classification c : classifications) {
						if (ANZSCO_ID.equals(c.getSchemeCode())) {
							q.setAnzsco(c.getValueCode());
						}
						else if (ASCO_ID.equals(c.getSchemeCode())) {
							q.setAsco(c.getValueCode());
						}
						else if (FIELD_OF_EDUCATION_ID.equals(c.getSchemeCode())) {
							q.setFieldOfEducation(c.getValueCode());
						}
						else if (LEVEL_OF_EDUCATION_ID.equals(c.getSchemeCode())) {
							q.setLevel(c.getValueCode());
						}
					}
					
					// committing to db every MAX_ENTRIES_TO_COMMIT entries
					if (entriesToCommit >= MAX_ENTRIES_TO_COMMIT) {
						created += context.newObjects().size();
						modified += context.modifiedObjects().size();
					
						context.commitChanges();
						this.ishVersion++;
						entriesToCommit = 0;
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
			
			created += context.newObjects().size();
			modified += context.modifiedObjects().size();
			context.commitChanges();
			
			result.setNumberOfNew(created);
			result.setNumberOfUpdated(modified);
		}
		catch (Exception e) {
			LOGGER.info("NTIS Qualifications update failed with exception.", e);
			throw new NTISException(e);
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
			
			int entriesToCommit = 0;
			int created = 0;
			int modified = 0;
			
			for (TrainingComponentSummary summary : searchResult.getResults().getValue().getTrainingComponentSummary()) {

				String type = summary.getComponentType().get(0);
				
				if (TRAINING_PACKAGE.equals(type)) {
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
					tp.setIshVersion(ishVersion);
					
					entriesToCommit += 1;
				
					detailsRequest.setCode(summary.getCode().getValue());
					TrainingComponent component = trainingService.getDetails(detailsRequest);
					if (component.getReleases() != null) {
						List<Release> releases = component.getReleases().getValue().getRelease();
						if (!releases.isEmpty() && releases.get(0).getComponents() != null) {
							List<ReleaseComponent> components = releases.get(0).getComponents().getValue().getReleaseComponent();
							for (ReleaseComponent c : components) {
								String code = c.getCode().getValue();
								String cType = c.getType().get(0);
								if (QUALIFICATION.equals(cType)) {
						
									SelectQuery q = new SelectQuery(Qualification.class);
									Expression e = ExpressionFactory.matchExp("nationalCode", code);
									q.setQualifier(e);
						
									List<Qualification> qual = context.performQuery(q);
									if (!qual.isEmpty()) {
										qual.get(0).setTrainingPackageId(tp.getId());
									}
								}
								else if (UNIT.equals(cType)) {
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
					}
					
					// committing to db every MAX_ENTRIES_TO_COMMIT entries
					if (entriesToCommit >= MAX_ENTRIES_TO_COMMIT) {
						created += context.newObjects().size();
						modified += context.modifiedObjects().size();
						
						context.commitChanges();
						this.ishVersion++;
						entriesToCommit = 0;
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
				
			created += context.newObjects().size();
			modified += context.modifiedObjects().size();
			context.commitChanges();
			
			result.setNumberOfNew(created);
			result.setNumberOfUpdated(modified);
		}
		catch (Exception e) {
			LOGGER.info("NTIS TrainingPackages update failed with exception.", e);
			throw new NTISException(e);
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
			
			int entriesToCommit = 0;
			int created = 0;
			int modified = 0;
			
			for (TrainingComponentSummary summary : searchResult.getResults().getValue().getTrainingComponentSummary()) {
				
				String type = summary.getComponentType().get(0);
				
				if (MODULE.equals(type) || UNIT.equals(type)) {
				
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
				
					if (MODULE.equals(summary.getComponentType().get(0))) {
						m.setIsModule((byte) 1);
					}
					else {
						m.setIsModule((byte) 0);
					}
					
					m.setIshVersion(ishVersion);
					
					entriesToCommit += 1;
				
					detailsRequest.setCode(summary.getCode().getValue());
					TrainingComponent component = trainingService.getDetails(detailsRequest);
					List<Classification> classifications = component.getClassifications().getValue().getClassification();
					for (Classification c : classifications) {
						if ("04".equals(c.getSchemeCode())) {
							m.setFieldOfEducation(c.getValueCode());
						}
					}
					
					// committing to db every MAX_ENTRIES_TO_COMMIT entries
					if (entriesToCommit > MAX_ENTRIES_TO_COMMIT) {
						created += context.newObjects().size();
						modified += context.modifiedObjects().size();
						
						context.commitChanges();
						this.ishVersion++;
						entriesToCommit = 0;
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
				
			created += context.newObjects().size();
			modified += context.modifiedObjects().size();
			context.commitChanges();
			
			result.setNumberOfNew(created);
			result.setNumberOfUpdated(modified);
		}
		catch (Exception e) {
			LOGGER.info("NTIS Modules update failed with exception.", e);
			throw new NTISException(e);
		}
		
		return result;
	}
	
}
