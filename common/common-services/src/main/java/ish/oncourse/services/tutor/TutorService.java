package ish.oncourse.services.tutor;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Tutor;
import ish.oncourse.services.BaseService;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.SelectQuery;

import java.util.Date;
import java.util.List;

@SuppressWarnings("deprecation")
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

		List<Tutor> result = findByQualifier(expr);

		return ((result != null) && !(result.isEmpty())) ? result.get(0) : null;
	}

	/**
	 * @return
	 */
	private Expression getSiteQualifier() {
		return ExpressionFactory.matchExp(Tutor.CONTACT_PROPERTY + "." + Contact.COLLEGE_PROPERTY,
				getWebSiteService().getCurrentCollege());
	}

	public Date getLatestModifiedDate() {
		return (Date) getCayenneService()
				.sharedContext()
				.performQuery(
						new EJBQLQuery("select max(t.modified) from Tutor t where "
								+ getSiteQualifier().toEJBQL("t"))).get(0);
	}

	public List<Tutor> getTutors() {
		return findByQualifier(getSiteQualifier());
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.tutor.ITutorService#findByAngelId(java.lang.Long)
	 */
	@Override
	public Tutor findByAngelId(Long angelId) {
		return (Tutor) getCayenneService().sharedContext().performQuery(
				new SelectQuery(Tutor.class, ExpressionFactory.matchExp(Tutor.ANGEL_ID_PROPERTY,
						angelId).andExp(getSiteQualifier())));
	}

    public boolean isActiveTutor(Tutor tutor)
    {
        return tutor.getFinishDate() == null || tutor.getFinishDate().after(new Date());
    }

}
