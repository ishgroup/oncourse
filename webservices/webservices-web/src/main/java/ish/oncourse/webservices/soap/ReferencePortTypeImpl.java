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
import ish.oncourse.services.reference.ITrainingPackageService;
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

	private static final Logger LOGGER = Logger.getLogger(ReferencePortTypeImpl.class);


	@Override
	public HashMap<String, Long> checkVersions() {

/*
		if (!hasHttpSession()) {
			throw new AuthenticationException("Service requires Authentication");
		}
 */

		Long version = null;
		HashMap<String, Long> versions = new HashMap<String, Long>();

		if (countryService == null) {
			LOGGER.error("Country Service is not initialised!");
		} else {
			version = countryService.findMaxIshVersion();
			if (version != null) {
				versions.put(Country.class.getName(), version);
			}
		}
		if (languageService == null) {
			LOGGER.error("Language Service is not initialised!");
		} else {
			version = languageService.findMaxIshVersion();
			if (version != null) {
				versions.put(Language.class.getName(), version);
			}
		}
		if (moduleService == null) {
			LOGGER.error("Module Service is not initialised!");
		} else {
			version = moduleService.findMaxIshVersion();
			if (version != null) {
				versions.put(Module.class.getName(), version);
			}
		}
		if (qualificationService == null) {
			LOGGER.error("Qualification Service is not initialised!");
		} else {
			version = qualificationService.findMaxIshVersion();
			if (version != null) {
				versions.put(Qualification.class.getName(), version);
			}
		}
		if (trainingPackageService == null) {
			LOGGER.error("Training Package Service is not initialised!");
		} else {
			version = trainingPackageService.findMaxIshVersion();
			if (version != null) {
				versions.put(TrainingPackage.class.getName(), version);
			}
		}

		return versions;
	}

/*	@Override
	public List<Country_Stub> getCountries(Long angelVersion, Integer batchNumber)
			throws AuthenticationException {

		List<Country_Stub> stubs = new ArrayList<Country_Stub>();
		List<Country> records = countryService.getForReplication(
				angelVersion);

		int startIndex = BATCH_SIZE * (batchNumber - 1);
		int endIndex = BATCH_SIZE * batchNumber;

		for (int i = startIndex; (i < endIndex) && (i < records.size()); i++) {
			stubs.add(CountryStubBuilder.convert(records.get(i)));
		}

		return stubs;
	}

	public List<Language_Stub> getLanguages(Long angelVersion, Integer batchNumber)
			throws AuthenticationException {

		List<Language_Stub> stubs = new ArrayList<Language_Stub>();
		List<Language> records = languageService.getForReplication(
				angelVersion);

		int startIndex = BATCH_SIZE * (batchNumber - 1);
		int endIndex = BATCH_SIZE * batchNumber;

		for (int i = startIndex; (i < endIndex) && (i < records.size()); i++) {
			stubs.add(LanguageStubBuilder.convert(records.get(i)));
		}

		return stubs;
	}

	public List<Module_Stub> getModules(Long angelVersion, Integer batchNumber)
			throws AuthenticationException {

		List<Module_Stub> stubs = new ArrayList<Module_Stub>();
		List<Module> records = moduleService.getForReplication(
				angelVersion);

		int startIndex = BATCH_SIZE * (batchNumber - 1);
		int endIndex = BATCH_SIZE * batchNumber;

		for (int i = startIndex; (i < endIndex) && (i < records.size()); i++) {
			stubs.add(ModuleStubBuilder.convert(records.get(i)));
		}

		return stubs;
	}

	public List<Qualification_Stub> getQualifications(Long angelVersion, Integer batchNumber)
			throws AuthenticationException {

		List<Qualification_Stub> stubs = new ArrayList<Qualification_Stub>();
		List<Qualification> records = qualificationService.getForReplication(
				angelVersion);

		int startIndex = BATCH_SIZE * (batchNumber - 1);
		int endIndex = BATCH_SIZE * batchNumber;

		for (int i = startIndex; (i < endIndex) && (i < records.size()); i++) {
			stubs.add(QualificationStubBuilder.convert(records.get(i)));
		}

		return stubs;
	}

	public List<TrainingPackage_Stub> getTrainingPackages(Long angelVersion, Integer batchNumber)
			throws AuthenticationException {

		List<TrainingPackage_Stub> stubs = new ArrayList<TrainingPackage_Stub>();
		List<TrainingPackage> records = trainingPackageService.getForReplication(
				angelVersion);

		int startIndex = BATCH_SIZE * (batchNumber - 1);
		int endIndex = BATCH_SIZE * batchNumber;

		for (int i = startIndex; (i < endIndex) && (i < records.size()); i++) {
			stubs.add(TrainingPackageStubBuilder.convert(records.get(i)));
		}

		return stubs;
	}*/
}
