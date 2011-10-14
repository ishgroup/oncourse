package ish.oncourse.services.reference;

import ish.oncourse.model.TrainingPackage;
/**
 * interface for Training Package Service
 * @author ksenia
 *
 */
public interface ITrainingPackageService extends IReferenceService<TrainingPackage> {
	/**
	 * Returns Training package with the given title if such a package exists, null otherwise
	 * @param name
	 * @return
	 */
	TrainingPackage getTrainingPackageByTitle(String title);
}
