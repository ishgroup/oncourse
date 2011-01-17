package ish.oncourse.webservices.soap.v4;

import javax.jws.WebService;

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
import ish.oncourse.webservices.soap.v4.stubs.reference.SoapReference_Stub;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Implementation of the ReferencePortTypeImpl Data API for the replication of ReferencePortTypeImpl
 * data set.
 * 
 * @see ReferencePortType
 *
 * @author Marek Wawrzyczny
 */
@WebService(endpointInterface = "ish.oncourse.webservices.soap.v4.ReferencePortType",
			serviceName = "ReferenceService",
			portName = "ReferencePort")
public class ReferencePortTypeImpl implements ReferencePortType {

	private static final int BATCH_SIZE = 100;

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


	@Override
	public Long getMaximumVersion() {

		Long version = null;

		IReferenceService<?> service = getAllServices().get(0);
		if (service == null) {
			LOGGER.error("Service is null!");
		} else {
			version = service.findMaxIshVersion();
		}

		return version;
	}

	@Override
	public List<SoapReference_Stub> getRecords(Long ishVersion) {

		List<SoapReference_Stub> stubs = new ArrayList<SoapReference_Stub>();

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

		return stubs;
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
