package ish.oncourse.services.reference;


import ish.oncourse.model.Module;
import java.util.List;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

/**
 * Implementation of the Module Service.
 *
 * @author Marek Wawrzyczny
 */
public class ModuleService extends ReferenceService<Module>
		implements IModuleService {

	@Override
	public Module getModuleByTitle(String title) {
		Expression qualifier = ExpressionFactory.matchExp(
				Module.TITLE_PROPERTY, title);
		List<Module> results = findByQualifier(qualifier);

		return results.isEmpty() ? null : results.get(0);
	}

}
