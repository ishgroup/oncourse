package ish.oncourse.services.reference;

import ish.oncourse.model.Qualification;
/**
 * interface for Qualification Service
 * @author ksenia
 *
 */
public interface IQualificationService extends IReferenceService<Qualification> {
	/**
	 * Returns Qualification with the given title if such a qualification exists, null otherwise
	 * @param title
	 * @return
	 */
	Qualification getQualificationByTitle(String title);
}
