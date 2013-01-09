package ish.oncourse.services.reference;

import ish.oncourse.model.Country;
/**
 * interface for Country Service
 * @author ksenia
 *
 */
public interface ICountryService extends IReferenceService<Country> {

	public static final String DEFAULT_COUNTRY_NAME = "Australia";
	/**
	 * Returns Country with the given name if such a country exists, null otherwise
	 * @param name
	 * @return
	 */
	Country getCountryByName(String name);
}
