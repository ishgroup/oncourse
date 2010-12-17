package ish.oncourse.services.reference;


import ish.oncourse.model.Qualification;
import java.util.List;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

/**
 * Implementation of the Qualification Service.
 *
 * @author Marek Wawrzyczny
 */
public class QualificationService extends ReferenceService<Qualification> 
		implements IQualificationService {

	@Override
	public Qualification getQualificationByTitle(String QualificationName) {
		Expression qualifier = ExpressionFactory.matchExp(
				Qualification.TITLE_PROPERTY, QualificationName);
		List<Qualification> results = findByQualifier(qualifier);
		return results.isEmpty() ? null : results.get(0);
	}

}
