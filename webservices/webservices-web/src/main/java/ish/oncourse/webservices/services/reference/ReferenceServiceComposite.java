package ish.oncourse.webservices.services.reference;

import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.services.reference.ILanguageService;
import ish.oncourse.services.reference.IModuleService;
import ish.oncourse.services.reference.IQualificationService;
import ish.oncourse.services.reference.IReferenceService;
import ish.oncourse.services.reference.ITrainingPackageService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.cayenne.Persistent;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

public class ReferenceServiceComposite implements IReferenceServiceComposite {

	private static final Logger LOGGER = Logger.getLogger(ReferenceServiceComposite.class);

	private List<IReferenceService<? extends Persistent>> allServices;

	/**
	 * Initialize all services for providing way to enumerate through.
	 * 
	 * @return
	 */
	@Autowired
	public ReferenceServiceComposite(@Inject ICountryService countryService, @Inject ILanguageService languageService,
			@Inject IQualificationService qualificationService, @Inject IModuleService moduleService,
			@Inject ITrainingPackageService trainingPackageService) {
		allServices = new ArrayList<IReferenceService<? extends Persistent>>();
		allServices.add(countryService);
		allServices.add(languageService);
		allServices.add(moduleService);
		allServices.add(qualificationService);
		allServices.add(trainingPackageService);
	}

	@Override
	public List<Persistent> getForReplication(Long ishVersion) {
		List<Persistent> list = new LinkedList<Persistent>();
		for (IReferenceService<? extends Persistent> service : allServices) {
			List<? extends Persistent> records = service.getForReplication(ishVersion);
			list.addAll(records);
		}
		return list;
	}

	@Override
	public Long findMaxIshVersion() {
		Long version = null;

		IReferenceService<?> service = allServices.get(0);
		if (service == null) {
			LOGGER.error("Service is null!");
		} else {
			version = service.findMaxIshVersion();
		}

		return version;
	}
}
