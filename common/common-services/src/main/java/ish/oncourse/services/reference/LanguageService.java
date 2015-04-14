/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.services.reference;

import ish.oncourse.model.Language;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import java.util.List;

/**
 * Implementation of the Language Service.
 * 
 * @author Marek Wawrzyczny
 */
public class LanguageService extends AbstractReferenceService<Language>
		implements ILanguageService {

	public Language getLanguageByName(String languageName) {
		Expression qualifier = ExpressionFactory.matchExp(
				Language.NAME_PROPERTY, languageName);
		List<Language> results = findByQualifier(qualifier);

		return results.isEmpty() ? null : results.get(0);
	}

}
