package ish.oncourse.services.reference;

import ish.oncourse.model.Language;

/**
 * interface for Language Service
 * @author ksenia
 *
 */

public interface ILanguageService {

	/**
	 * Returns Language with the given name if such a language exists, null otherwise
	 * @param languageName
	 * @return
	 */
	Language getLanguageByName(String languageName);
	
}
