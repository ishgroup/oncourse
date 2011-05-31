package ish.oncourse.webservices.reference.services;

import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.services.reference.ILanguageService;
import ish.oncourse.services.reference.IModuleService;
import ish.oncourse.services.reference.IQualificationService;
import ish.oncourse.services.reference.IReferenceService;
import ish.oncourse.services.reference.ITrainingPackageService;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.cayenne.Persistent;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author anton
 * 
 */

public class ReferenceService {

	private List<IReferenceService<? extends Persistent>> allServices;

	/**
	 * Initialize all services for providing way to enumerate through.
	 * 
	 * @return
	 */
	@Autowired
	public ReferenceService(@Inject ICountryService countryService, @Inject ILanguageService languageService,
			@Inject IQualificationService qualificationService, @Inject IModuleService moduleService, @Inject ITrainingPackageService trainingPackageService) {
		allServices = new LinkedList<IReferenceService<? extends Persistent>>();
		allServices.add(countryService);
		allServices.add(languageService);
		allServices.add(trainingPackageService);
		allServices.add(moduleService);
		allServices.add(qualificationService);
	}

	/**
	 * 
	 */
	public List<Persistent> getForReplication(Long ishVersion) {
		List<Persistent> list = new LinkedList<Persistent>();
		for (IReferenceService<? extends Persistent> service : allServices) {
			List<? extends Persistent> records = service.getForReplication(ishVersion);
			list.addAll(records);
		}
		return list;
	}

	/**
	 * Finds maximun ishVersion accross all Reference services, thus accross all
	 * reference entities.
	 */
	public Long findMaxIshVersion() {
		SortedSet<Long> versions = new TreeSet<Long>();

		for (IReferenceService<?> service : allServices) {
			Long maxVersion = service.findMaxIshVersion();
			
			if (maxVersion != null) {
				versions.add(maxVersion);
			}
			
		}

		return (versions.size() > 0) ? versions.last() : 0;
	}
}
