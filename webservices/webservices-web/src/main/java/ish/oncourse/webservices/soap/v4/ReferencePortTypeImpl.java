package ish.oncourse.webservices.soap.v4;

import ish.oncourse.model.Country;
import ish.oncourse.model.Language;
import ish.oncourse.model.Module;
import ish.oncourse.model.Qualification;
import ish.oncourse.model.TrainingPackage;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.services.reference.ILanguageService;
import ish.oncourse.services.reference.IModuleService;
import ish.oncourse.services.reference.IQualificationService;
import ish.oncourse.services.reference.IReferenceService;
import ish.oncourse.services.reference.ITrainingPackageService;
import ish.oncourse.webservices.soap.v4.builders.CountryStubBuilder;
import ish.oncourse.webservices.soap.v4.builders.LanguageStubBuilder;
import ish.oncourse.webservices.soap.v4.builders.ModuleStubBuilder;
import ish.oncourse.webservices.soap.v4.builders.QualificationStubBuilder;
import ish.oncourse.webservices.soap.v4.builders.TrainingPackageStubBuilder;
import ish.oncourse.webservices.v4.stubs.reference.ReferenceResult;
import ish.oncourse.webservices.v4.stubs.reference.SoapReferenceStub;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * The ReferencePortType defines the API for one-way (Willow to Angel)
 * replication of Reference Data.
 *
 * <p>Reference Data consist of the following Entities:</p>
 *
 * <ul>
 *		<li>@see ish.oncourse.model.Country</li>
 *		<li>@see ish.oncourse.model.Language</li>
 *		<li>@see ish.oncourse.model.Module</li>
 *		<li>@see ish.oncourse.model.Qualification</li>
 *		<li>@see ish.oncourse.model.TrainingPackage</li>
 * </ul>
 *
 * <p>This data is sent to all customers and thus requires that the service is
 * authenticated for tracking purposes (IP/security code) but does not require
 * web services to be setup for the client.</p>
 *
 * <p>The procedure is as follows:</p>
 *
 * <ol>
 *		<li>Call @see #getMaximumVersion() to get the latest Willow version
 *			number</li>
 *		<li>Loop through the missing versions, starting with the lowest
 *			through to the highest.<br/><strong>Remember to save the received
 *			lists between subsequent calls.</strong>
 *		</li>
 * </ol>
 *
 * Please note that the get calls return the <b>entire</b> set of records for
 * that version.
 *
 * @author Marek Wawrzyczny
 */
@WebService(endpointInterface = "ish.oncourse.webservices.soap.v4.ReferencePortType",
			serviceName = "ReferenceService",
			portName = "ReferencePort", targetNamespace="http://ref.v4.soap.webservices.oncourse.ish/")
public class ReferencePortTypeImpl implements ReferencePortType {

	@Inject @Autowired
	private ICountryService countryService;
	@Inject @Autowired
	private ILanguageService languageService;
	@Inject @Autowired
	private IModuleService moduleService;
	@Inject @Autowired
	private IQualificationService qualificationService;
	@Inject @Autowired
	private ITrainingPackageService trainingPackageService;
	
	private List<IReferenceService<?>> allServices;

	private static final Logger LOGGER = Logger.getLogger(ReferencePortTypeImpl.class);

	
	/**
	 * Call to find out the most recent version of Reference Data - note that
	 * this is shared across tables.
	 *
	 * @return Map the max version
	 */
	@Override
	public long getMaximumVersion() {

		Long version = null;

		IReferenceService<?> service = getAllServices().get(0);
		if (service == null) {
			LOGGER.error("Service is null!");
		} else {
			version = service.findMaxIshVersion();
		}

		return version;
	}
	
	/**
	 * Call to get all records for this version.
	 *
	 * @param ishVersion - the version of records to fetch
	 *
	 * @return all records with this version or empty list if no records with
	 *		that version exist
	 */
	@Override
	public ReferenceResult getRecords(long ishVersion) {

		List<SoapReferenceStub> stubs = new ArrayList<SoapReferenceStub>();

		// FIXME: Iterating through services can be done generically
		List<Country> cRecords = countryService.getForReplication(ishVersion);
		for (Country record : cRecords) {
			stubs.add(CountryStubBuilder.convert(record));
		}

		List<Language> lRecords = languageService.getForReplication(ishVersion);
		for (Language record : lRecords) {
			stubs.add(LanguageStubBuilder.convert(record));
		}

		List<Module> mRecords = moduleService.getForReplication(ishVersion);
		for (Module record : mRecords) {
			stubs.add(ModuleStubBuilder.convert(record));
		}

		List<Qualification> qRecords = qualificationService.getForReplication(ishVersion);
		for (Qualification record : qRecords) {
			stubs.add(QualificationStubBuilder.convert(record));
		}

		List<TrainingPackage> tpRecords = trainingPackageService.getForReplication(ishVersion);
		for (TrainingPackage record : tpRecords) {
			stubs.add(TrainingPackageStubBuilder.convert(record));
		}
		
		ReferenceResult result = new ReferenceResult();
		result.getCountryOrLanguageOrModule().addAll(stubs);

		return result;
	}

	/**
	 * Helper method for providing way to enumerate through all services.
	 *
	 * @return
	 */
	private List<IReferenceService<?>> getAllServices() {
		if (allServices == null) {
			allServices = new ArrayList<IReferenceService<?>>();
			allServices.add(countryService);
			allServices.add(languageService);
			allServices.add(moduleService);
			allServices.add(qualificationService);
			allServices.add(trainingPackageService);
		}

		return allServices;
	}

}
