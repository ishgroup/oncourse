package ish.oncourse.admin.services.ntis;

import ish.oncourse.listeners.IshVersionHolder;
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

import org.apache.cayenne.Cayenne;
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
	
	private static final int RESULTS_PAGE_SIZE = 1000;

	private static final Logger LOGGER = Logger.getLogger(NTISUpdaterImpl.class);

	@Inject
	private ITrainingComponentService trainingService;

	@Inject
	private ReferenceService referenceService;

	@Inject
	private ICayenneService cayenneService;

	/*
	 * 
	 * @see ish.oncourse.admin.services.ntis.INTISUpdateService#doUpdate()
	 */
	@Override
	public NTISResult doUpdate(Date from, Date to, Class<?> type) throws NTISException {

		try {

			GregorianCalendar cal = new GregorianCalendar();

			DateTimeOffset fromDate = new DateTimeOffset();
			DateTimeOffset toDate = new DateTimeOffset();

			cal.setTime(from);
			fromDate.setDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));
			cal.setTime(to);
			toDate.setDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));

			if (type == Qualification.class) {
				return updateQualifications(fromDate, toDate);
			} else if (type == TrainingPackage.class) {
				return updateTrainingPackages(fromDate, toDate);
			} else if (type == Module.class) {
				return updateModules(fromDate, toDate);
			}

		} catch (DatatypeConfigurationException e) {
			throw new NTISException();
		}

		return null;

	}

	/**
	 * Updates qualifications.
	 * 
	 * @param from
	 *            from date
	 * @param to
	 *            to date
	 * @return update result
	 * @throws NTISException
	 */
	private NTISResult updateQualifications(DateTimeOffset from, DateTimeOffset to) throws NTISException {

		ObjectContext context = cayenneService.newNonReplicatingContext();

		ObjectFactory objectFactory = new ObjectFactory();

		TrainingComponentTypeFilter typeFilter = new TrainingComponentTypeFilter();
		typeFilter.setIncludeQualification(true);
		typeFilter.setIncludeAccreditedCourse(true);

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

		TrainingComponentInformationRequested info = new TrainingComponentInformationRequested();
		info.setShowClassifications(true);

		TrainingComponentDetailsRequest detailsRequest = new TrainingComponentDetailsRequest();
		detailsRequest.setInformationRequest(objectFactory.createTrainingComponentInformationRequested(info));

		try {
			
			int totalNew = 0;
			int totalModified = 0;
			int pageNumber = 1;
			int pageCount = 0;

			do {
				request.setPageNumber(pageNumber);
				TrainingComponentSearchResult searchResult = trainingService.searchByModifiedDate(request);
			
				pageCount = (int) Math.ceil((double) searchResult.getCount() / RESULTS_PAGE_SIZE);
				
				int numberOfNew = 0;
				
				for (TrainingComponentSummary summary : searchResult.getResults().getValue().getTrainingComponentSummary()) {
					
					String type = summary.getComponentType().get(0);
					
					if (QUALIFICATION.equals(type) || ACCREDITED_COURSE.equals(type)) {
						
						SelectQuery query = new SelectQuery(Qualification.class);
						Expression exp = ExpressionFactory.matchExp("nationalCode", summary.getCode().getValue());
						query.setQualifier(exp);
						
						Qualification q = (Qualification) Cayenne.objectForQuery(context, query);
						
						if (q == null) {
							q = context.newObject(Qualification.class);
							numberOfNew++;
						}
						
						q.setNationalCode(summary.getCode().getValue());
						q.setTitle(summary.getTitle().getValue());
						
						detailsRequest.setCode(summary.getCode().getValue());
						TrainingComponent component = trainingService.getDetails(detailsRequest);
						List<Classification> classifications = component.getClassifications().getValue().getClassification();
						for (Classification c : classifications) {
							if (ANZSCO_ID.equals(c.getSchemeCode())) {
								q.setAnzsco(c.getValueCode());
							} else if (ASCO_ID.equals(c.getSchemeCode())) {
								q.setAsco(c.getValueCode());
							} else if (FIELD_OF_EDUCATION_ID.equals(c.getSchemeCode())) {
								q.setFieldOfEducation(c.getValueCode());
							} else if (LEVEL_OF_EDUCATION_ID.equals(c.getSchemeCode())) {
								q.setLevel(getEducationLevelName(c.getValueCode()));
							}
						}
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
				SelectQuery query = new SelectQuery(Qualification.class);
				Expression exp = ExpressionFactory.matchExp("nationalCode", c.getNationalCode().getValue());
				query.setQualifier(exp);

				Qualification r = (Qualification) Cayenne.objectForQuery(context, query);

				if (r != null) {
					context.deleteObject(r);
				}
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
	
	private String getEducationLevelName(String levelCode) {
		if ("211".equals(levelCode)) {
			return "Graduate Diploma";
		}
		else if ("213".equals(levelCode)) {
			return "Graduate Diploma (professional specialist)";
		}
		else if ("221".equals(levelCode)) {
			return "Graduate Certificate";
		}
		else if ("222".equals(levelCode)) {
			return "Graduate Certificate (professional specialist)";
		}
		else if ("311".equals(levelCode)) {
			return "Bachelor degree (honours)";
		}
		else if ("312".equals(levelCode)) {
			return "Bachelor degree";
		}
		else if ("411".equals(levelCode)) {
			return "Advanced Diploma";
		}
		else if ("413".equals(levelCode)) {
			return "Associate Degree";
		}
		else if ("421".equals(levelCode)) {
			return "Diploma";
		}
		else if ("511".equals(levelCode)) {
			return "Certificate IV";
		}
		else if ("514".equals(levelCode)) {
			return "Certificate III";
		}
		else if ("521".equals(levelCode)) {
			return "Certificate II";
		}
		else if ("524".equals(levelCode)) {
			return "Certificate I";
		}
		else if ("611".equals(levelCode)) {
			return "Year 12";
		}
		else if ("613".equals(levelCode)) {
			return "Year 11";
		}
		else if ("621".equals(levelCode)) {
			return "Year 10";
		}
		else {
			return levelCode;
		}
	}

	/**
	 * Save changes to database, increase ishVersion and saves to thread local.
	 * 
	 * @param numberOfNew
	 *            number of new records.
	 * @param context
	 *            object context
	 * @return number of modified records
	 */
	private long saveChanges(int numberOfNew, ObjectContext context) {

		Long ishVersion = referenceService.findMaxIshVersion() + 1;
		long modified = 0;

		try {
			IshVersionHolder.setIshVersion(ishVersion);
			context.commitChanges();

			long allTouchedRecords = referenceService.getNumberOfRecordsForIshVersion(ishVersion);

			if (allTouchedRecords >= numberOfNew) {
				modified = allTouchedRecords - numberOfNew;
			}
		} finally {
			IshVersionHolder.cleanUp();
		}

		return modified;
	}

	/**
	 * Updates training packages.
	 * 
	 * @param from
	 *            from date
	 * @param to
	 *            to date
	 * @return update result
	 * @throws NTISException
	 * 
	 */
	private NTISResult updateTrainingPackages(DateTimeOffset from, DateTimeOffset to) throws NTISException {

		ObjectContext context = cayenneService.newNonReplicatingContext();

		ObjectFactory objectFactory = new ObjectFactory();

		TrainingComponentTypeFilter typeFilter = new TrainingComponentTypeFilter();
		typeFilter.setIncludeTrainingPackage(true);

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

		TrainingComponentInformationRequested info = new TrainingComponentInformationRequested();
		info.setShowComponents(true);
		info.setShowReleases(true);

		TrainingComponentDetailsRequest detailsRequest = new TrainingComponentDetailsRequest();
		detailsRequest.setInformationRequest(objectFactory.createTrainingComponentInformationRequested(info));

		try {

			int totalNew = 0;
			int totalModified = 0;
			int pageNumber = 1;
			int pageCount = 0;
			
			do {
				request.setPageNumber(pageNumber);
				TrainingComponentSearchResult searchResult = trainingService.searchByModifiedDate(request);
			
				pageCount = (int) Math.ceil((double) searchResult.getCount() / RESULTS_PAGE_SIZE);
				
				int numberOfNew = 0;
				
				for (TrainingComponentSummary summary : searchResult.getResults().getValue().getTrainingComponentSummary()) {
					
					String type = summary.getComponentType().get(0);
					
					if (TRAINING_PACKAGE.equals(type)) {
						SelectQuery query = new SelectQuery(TrainingPackage.class);
						Expression exp = ExpressionFactory.matchExp("nationalISC", summary.getCode().getValue());
						query.setQualifier(exp);
						
						TrainingPackage tp = (TrainingPackage) Cayenne.objectForQuery(context, query);
						if (tp == null) {
							tp = context.newObject(TrainingPackage.class);
							numberOfNew++;
						}
						
						tp.setNationalISC(summary.getCode().getValue());
						tp.setTitle(summary.getTitle().getValue());
						tp.setType(summary.getComponentType().get(0));
						
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
										
										Qualification qual = (Qualification) Cayenne.objectForQuery(context, query);
										
										if (qual != null) {
											qual.setTrainingPackageId(tp.getId());
										}
										
									} else if (UNIT.equals(cType)) {
										SelectQuery q = new SelectQuery(Module.class);
										Expression e = ExpressionFactory.matchExp("nationalCode", code);
										q.setQualifier(e);
										
										Module module = (Module) Cayenne.objectForQuery(context, q);
										
										if (module != null) {
											module.setTrainingPackageId(tp.getId());
										}
									}
								}
							}
						}
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
				SelectQuery query = new SelectQuery(TrainingPackage.class);
				Expression exp = ExpressionFactory.matchExp("nationalISC", c.getNationalCode().getValue());
				query.setQualifier(exp);

				TrainingPackage r = (TrainingPackage) Cayenne.objectForQuery(context, query);
				if (r != null) {
					context.deleteObject(r);
				}
			}

			totalModified += saveChanges(totalNew, context);
			
			NTISResult result = new NTISResult();
			result.setNumberOfNew(totalNew);
			result.setNumberOfUpdated(totalModified);
			return result;

		} catch (Exception e) {
			LOGGER.info("NTIS TrainingPackages update failed with exception.", e);
			throw new NTISException(e);
		}
	}

	/**
	 * Updates modules.
	 * 
	 * @param from
	 *            from date
	 * @param to
	 *            to date
	 * @return update result
	 * @throws NTISException
	 * 
	 */
	private NTISResult updateModules(DateTimeOffset from, DateTimeOffset to) throws NTISException {

		ObjectContext context = cayenneService.newNonReplicatingContext();

		ObjectFactory objectFactory = new ObjectFactory();

		TrainingComponentTypeFilter typeFilter = new TrainingComponentTypeFilter();
		typeFilter.setIncludeUnit(true);
		typeFilter.setIncludeAccreditedCourseModule(true);

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

		TrainingComponentInformationRequested info = new TrainingComponentInformationRequested();
		info.setShowClassifications(true);

		TrainingComponentDetailsRequest detailsRequest = new TrainingComponentDetailsRequest();
		detailsRequest.setInformationRequest(objectFactory.createTrainingComponentInformationRequested(info));

		try {

			int totalNew = 0;
			int totalModified = 0;
			int pageNumber = 1;
			int pageCount = 0;
			
			do {
				request.setPageNumber(pageNumber);
				TrainingComponentSearchResult searchResult = trainingService.searchByModifiedDate(request);
				
				pageCount = (int) Math.ceil((double) searchResult.getCount() / RESULTS_PAGE_SIZE);
				int numberOfNew = 0;

				for (TrainingComponentSummary summary : searchResult.getResults().getValue().getTrainingComponentSummary()) {
					
					String type = summary.getComponentType().get(0);
					
					if (MODULE.equals(type) || UNIT.equals(type)) {

						SelectQuery query = new SelectQuery(Module.class);
						Expression exp = ExpressionFactory.matchExp("nationalCode", summary.getCode().getValue());
						query.setQualifier(exp);

						Module m = (Module) Cayenne.objectForQuery(context, query);
						
						if (m == null) {
							m = context.newObject(Module.class);
							numberOfNew++;
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
							if ("04".equals(c.getSchemeCode())) {
								m.setFieldOfEducation(c.getValueCode());
							}
						}
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
				SelectQuery query = new SelectQuery(Module.class);
				Expression exp = ExpressionFactory.matchExp("nationalCode", c.getNationalCode().getValue());
				query.setQualifier(exp);

				Module r = (Module) Cayenne.objectForQuery(context, query);

				if (r != null) {
					context.deleteObject(r);
				}
			}

			totalModified += saveChanges(totalNew, context);
			
			NTISResult result = new NTISResult();
			result.setNumberOfNew(totalNew);
			result.setNumberOfUpdated(totalModified);
			return result;
		} catch (Exception e) {
			LOGGER.info("NTIS Modules update failed with exception.", e);
			throw new NTISException(e);
		}
	}
}
