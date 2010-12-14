package ish.oncourse.webservices.soap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jws.WebService;
import javax.servlet.ServletContext;

import ish.oncourse.model.Country;
import ish.oncourse.model.Language;
import ish.oncourse.model.Module;
import ish.oncourse.model.Qualification;
import ish.oncourse.model.TrainingPackage;
import ish.oncourse.services.reference.CountryService;
import ish.oncourse.services.reference.LanguageService;
import ish.oncourse.services.reference.ModuleService;
import ish.oncourse.services.reference.QualificationService;
import ish.oncourse.services.reference.TrainingPackageService;
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
import javax.ws.rs.core.Context;
import org.apache.log4j.Logger;
import org.apache.tapestry5.TapestryFilter;
import org.apache.tapestry5.ioc.Registry;


/**
 * Implementation of the ReferenceService Data API for the replication of ReferenceService
 * data set.
 * 
 * @see IReferenceService
 *
 * @author Marek Wawrzyczny
 */
@WebService(endpointInterface = "ish.oncourse.webservices.soap.IReferenceService",
			serviceName = "ReferenceService",
			portName = "ReferencePort")
public class ReferenceService /*extends AbstractAuthenticatedService*/
		implements IReferenceService {

	private static final int BATCH_SIZE = 100;

	@Context
	private ServletContext servletContext;
	private CountryService countryService;
/*	@Inject
	LanguageService languageService;
	@Inject
	ModuleService moduleService;
	@Inject
	QualificationService qualificationService;
	@Inject
	TrainingPackageService trainingPackageService;
*/
	private static final Logger LOGGER = Logger.getLogger(ReferenceService.class);


	@Override
	public HashMap<String, Long> checkVersions() {

/*		if (!hasHttpSession()) {
//			throw new AuthenticationException("Service requires Authentication");
		}*/

		HashMap<String, Long> versions = new HashMap<String, Long>();

		Registry registry = (Registry) servletContext.getAttribute(
				TapestryFilter.REGISTRY_CONTEXT_NAME);

		if (countryService == null) {
			countryService = registry.getService(CountryService.class);
		}
		if (countryService == null) {
			LOGGER.debug("Country Service not initialised!!!");
		}
		Long version = countryService.findMaxIshVersion();
		if (version != null) {
			versions.put(Country.class.getName(), version);
		}
/*		version = languageService.findMaxIshVersion();
		if (version != null) {
			versions.put(Language.class.getName(), version);
		}
		version = moduleService.findMaxIshVersion();
		if (version != null) {
			versions.put(Module.class.getName(), version);
		}
		version = qualificationService.findMaxIshVersion();
		if (version != null) {
			versions.put(Qualification.class.getName(), version);
		}
		version = trainingPackageService.findMaxIshVersion();
		if (version != null) {
			versions.put(TrainingPackage.class.getName(), version);
		}*/

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
