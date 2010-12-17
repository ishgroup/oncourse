/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.services.reference;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import ish.oncourse.model.Language;

/**
 * Implementation of the Language Service.
 * 
 * @author Marek Wawrzyczny
 */
public class LanguageService extends ReferenceService<Language>
		implements ILanguageService {

	@Override
	public Language getLanguageByName(String languageName) {
		Expression qualifier = ExpressionFactory.matchExp(
				Language.NAME_PROPERTY, languageName);
		List<Language> results = findByQualifier(qualifier);

		return results.isEmpty() ? null : results.get(0);
	}

}
