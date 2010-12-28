package ish.oncourse.services.tutor;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Tutor;
import ish.oncourse.services.BaseService;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.EJBQLQuery;

public class TutorService extends BaseService<Tutor> implements ITutorService {

	/**
	 * Tutor lookup based on their ID.
	 * 
	 * @param tutorId
	 *            record ID
	 * @return Tutor if found or null
	 */
	public Tutor getTutorById(Long tutorId) {

		Expression expr = ExpressionFactory.matchExp(
				Tutor.CONTACT_PROPERTY + "." + Contact.COLLEGE_PROPERTY,
				getWebSiteService().getCurrentCollege()).andExp(
				ExpressionFactory.matchExp(Tutor.ANGEL_ID_PROPERTY, tutorId));

		@SuppressWarnings("unchecked")
		List<Tutor> result = findByQualifier(expr);

		return ((result != null) && !(result.isEmpty())) ? result.get(0) : null;
	}

	/**
	 * @return
	 */
	private Expression getSiteQualifier() {
		return ExpressionFactory.matchExp(Tutor.CONTACT_PROPERTY + "."
				+ Contact.COLLEGE_PROPERTY, getWebSiteService()
				.getCurrentCollege());
	}

	public Date getLatestModifiedDate() {
		return (Date) getCayenneService()
				.sharedContext()
				.performQuery(
						new EJBQLQuery(
								"select max(t.modified) from Tutor t where "
										+ getSiteQualifier().toEJBQL("t")))
				.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<Tutor> getTutors() {
		return findByQualifier(getSiteQualifier());
	}
}
