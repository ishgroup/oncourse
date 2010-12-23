/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.services.reference;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import ish.oncourse.model.Country;

/**
 * Implementation of the Country Service.
 * 
 * @author Marek Wawrzyczny
 */
public class CountryService extends ReferenceService<Country>
		implements ICountryService {

	public Country getCountryByName(String countryName) {
		Expression qualifier = ExpressionFactory.matchExp(
				Country.NAME_PROPERTY, countryName);
		List<Country> results = findByQualifier(qualifier);

		return results.isEmpty() ? null : results.get(0);
	}
	
}
