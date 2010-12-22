package ish.oncourse.webservices.soap;

import java.util.HashMap;

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
import ish.oncourse.webservices.soap.builders.CountryStubBuilder;
import ish.oncourse.webservices.soap.builders.LanguageStubBuilder;
import ish.oncourse.webservices.soap.builders.ModuleStubBuilder;
import ish.oncourse.webservices.soap.builders.QualificationStubBuilder;
import ish.oncourse.webservices.soap.builders.TrainingPackageStubBuilder;
import ish.oncourse.webservices.soap.stubs.Country_Stub;
import ish.oncourse.webservices.soap.stubs.Language_Stub;
import ish.oncourse.webservices.soap.stubs.Module_Stub;
import ish.oncourse.webservices.soap.stubs.Qualification_Stub;
import ish.oncourse.webservices.soap.stubs.TrainingPackage_Stub;
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
@WebService(endpointInterface = "ish.oncourse.webservices.soap.ReferencePortType",
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
	public HashMap<String, Long> checkVersions() {

		Long version = null;
		HashMap<String, Long> versions = new HashMap<String, Long>();

		for (IReferenceService<?> service : getAllServices()) {
			if (service == null) {
				LOGGER.error("Service is null!");
			} else {
				version = service.findMaxIshVersion();
				if (version != null) {
					versions.put(service.getEntityClass().getSimpleName(), version);
				}
			}
		}

		return versions;
	}

	@Override
	public List<Country_Stub> getCountries(Long ishVersion) {

		List<Country_Stub> stubs = new ArrayList<Country_Stub>();
		List<Country> records = countryService.getForReplication(ishVersion);

		for (Country record : records) {
			stubs.add(CountryStubBuilder.convert(record));
		}

		return stubs;
	}

	@Override
	public List<Language_Stub> getLanguages(Long ishVersion) {

		List<Language_Stub> stubs = new ArrayList<Language_Stub>();
		List<Language> records = languageService.getForReplication(ishVersion);

		for (Language record : records) {
			stubs.add(LanguageStubBuilder.convert(record));
		}

		return stubs;
	}

	@Override
	public List<Module_Stub> getModules(Long ishVersion) {

		List<Module_Stub> stubs = new ArrayList<Module_Stub>();
		List<Module> records = moduleService.getForReplication(ishVersion);

		for (Module record : records) {
			stubs.add(ModuleStubBuilder.convert(record));
		}

		return stubs;
	}

	@Override
	public List<Qualification_Stub> getQualifications(Long ishVersion) {

		List<Qualification_Stub> stubs = new ArrayList<Qualification_Stub>();
		List<Qualification> records = qualificationService.getForReplication(
				ishVersion);

		for (Qualification record : records) {
			stubs.add(QualificationStubBuilder.convert(record));
		}

		return stubs;
	}

	@Override
	public List<TrainingPackage_Stub> getTrainingPackages(Long ishVersion) {

		List<TrainingPackage_Stub> stubs = new ArrayList<TrainingPackage_Stub>();
		List<TrainingPackage> records = trainingPackageService.getForReplication(
				ishVersion);

		for (TrainingPackage record : records) {
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
