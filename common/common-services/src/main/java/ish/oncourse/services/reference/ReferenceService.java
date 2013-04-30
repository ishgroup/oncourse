package ish.oncourse.services.reference;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.cayenne.Persistent;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * The services which combines the results across all individual reference service.
 * @author anton
 * 
 */

public class ReferenceService {
	
	/**
	 * The list with all reference services.
	 */
	private List<IReferenceService<? extends Persistent>> allServices;

	/**
	 * Initialize all services for providing way to enumerate through.
	 * 
	 * @return
	 */
	@Inject
	public ReferenceService(ICountryService countryService, ILanguageService languageService, IQualificationService qualificationService,
			IModuleService moduleService, ITrainingPackageService trainingPackageService) {
		allServices = new LinkedList<>();
		allServices.add(countryService);
		allServices.add(languageService);
		allServices.add(trainingPackageService);
		allServices.add(moduleService);
		allServices.add(qualificationService);
	}

	/**
	 * Combines the results accross all reference services for ishVersion.
	 */
	public List<Persistent> getForReplication(Long ishVersion) {
		List<Persistent> list = new LinkedList<>();
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
		SortedSet<Long> versions = new TreeSet<>();

		for (IReferenceService<?> service : allServices) {
			Long maxVersion = service.findMaxIshVersion();

			if (maxVersion != null) {
				versions.add(maxVersion);
			}

		}

		return (versions.size() > 0) ? versions.last() : 0;
	}
	
	/**
	 * The total number of records with ishVersion across all reference services.
	 * @param ishVersion ishVersion
	 * @return number of records.
	 */
	public Long getNumberOfRecordsForIshVersion(Long ishVersion) {
		long sum = 0;
		for (IReferenceService<?> service : allServices) {
			sum += service.getNumberOfRecordsForIshVersion(ishVersion);
		}
		return sum;
	}
}
