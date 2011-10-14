package ish.oncourse.services.reference;

import ish.oncourse.model.Language;

/**
 * interface for Language Service
 * @author ksenia
 *
 */

public interface ILanguageService extends IReferenceService<Language>{

	/**
	 * Returns Language with the given name if such a language exists, null otherwise
	 * @param name
	 * @return
	 */
	Language getLanguageByName(String name);
	
}
