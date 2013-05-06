package ish.oncourse.services.reference;


import ish.common.types.QualificationType;
import ish.oncourse.model.Qualification;
import java.util.List;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

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

	@Override
	protected Expression getQueryQualifier(Long ishVersion) {
		return super.getQueryQualifier(ishVersion)
				.andExp(ExpressionFactory.noMatchExp(
						Qualification.IS_ACCREDITED_COURSE_PROPERTY,
						QualificationType.SKILLSET_TYPE));
	}
}
