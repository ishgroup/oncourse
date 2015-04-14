/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.services.reference;

import ish.oncourse.model.Country;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import java.util.List;

/**
 * Implementation of the Country Service.
 * 
 * @author Marek Wawrzyczny
 */
public class CountryService extends AbstractReferenceService<Country>
		implements ICountryService {

	public Country getCountryByName(String countryName) {
		Expression qualifier = ExpressionFactory.matchExp(
				Country.NAME_PROPERTY, countryName);
		List<Country> results = findByQualifier(qualifier);

		return results.isEmpty() ? null : results.get(0);
	}
	
}
