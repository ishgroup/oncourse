package ish.oncourse.services.reference;

import org.apache.cayenne.Persistent;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The services which combines the results across all individual reference service.
 * @author anton
 * 
 */

public abstract class ReferenceService {
	
	/**
	 * The list with all reference services.
	 */
	protected List<IReferenceService<? extends Persistent>> allServices;

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

		return (versions.size() > 0) ? versions.last() : 0L;
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
