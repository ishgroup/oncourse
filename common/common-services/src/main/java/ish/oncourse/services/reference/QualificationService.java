package ish.oncourse.services.reference;


import ish.oncourse.model.Qualification;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import java.util.List;

/**
 * Implementation of the Qualification Service.
 *
 * @author Marek Wawrzyczny
 */
public class QualificationService extends AbstractReferenceService<Qualification> 
		implements IQualificationService {

	public Qualification getQualificationByTitle(String QualificationName) {
		Expression qualifier = ExpressionFactory.matchExp(
				Qualification.TITLE_PROPERTY, QualificationName);
		List<Qualification> results = findByQualifier(qualifier);
		return results.isEmpty() ? null : results.get(0);
	}
}
