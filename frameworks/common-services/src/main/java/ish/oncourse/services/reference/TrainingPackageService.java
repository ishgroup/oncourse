package ish.oncourse.services.reference;

import ish.oncourse.model.TrainingPackage;
import java.util.List;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;


/**
 * Implementation of the Training Package Service.
 *
 * @author Marek Wawrzyczny
 */
public class TrainingPackageService extends ReferenceService<TrainingPackage>
		implements ITrainingPackageService {

	@Override
	public TrainingPackage getTrainingPackageByTitle(String name) {
		Expression qualifier = ExpressionFactory.matchExp(
				TrainingPackage.TITLE_PROPERTY, name);
		List<TrainingPackage> results = findByQualifier(qualifier);

		return results.isEmpty() ? null : results.get(0);
	}
}
