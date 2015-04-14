package ish.oncourse.services.reference;


import ish.oncourse.model.Module;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import java.util.List;

/**
 * Implementation of the Module Service.
 *
 * @author Marek Wawrzyczny
 */
public class ModuleService extends AbstractReferenceService<Module>
		implements IModuleService {

	public Module getModuleByTitle(String title) {
		Expression qualifier = ExpressionFactory.matchExp(
				Module.TITLE_PROPERTY, title);
		List<Module> results = findByQualifier(qualifier);

		return results.isEmpty() ? null : results.get(0);
	}

}
