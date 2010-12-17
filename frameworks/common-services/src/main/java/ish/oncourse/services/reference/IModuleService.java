package ish.oncourse.services.reference;

import ish.oncourse.model.Module;
/**
 * interface for Module Service
 * @author ksenia
 *
 */
public interface IModuleService extends IReferenceService<Module> {
	/**
	 * Returns Module with the given title if such a module exists, null otherwise
	 * @param title
	 * @return
	 */
	Module getModuleByTitle(String title);
}
