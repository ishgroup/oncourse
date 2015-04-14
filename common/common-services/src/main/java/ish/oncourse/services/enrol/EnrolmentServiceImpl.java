package ish.oncourse.services.enrol;

import ish.oncourse.model.Enrolment;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

public class EnrolmentServiceImpl implements IEnrolmentService {

	@Inject
	private ICayenneService cayenneService;

	@Override
	public Enrolment loadById(Long id) {

		SelectQuery q = new SelectQuery(Enrolment.class);
		q.andQualifier(ExpressionFactory.matchDbExp(Enrolment.ID_PK_COLUMN, id));
		q.addPrefetch(Enrolment.COURSE_CLASS_PROPERTY);

		return (Enrolment) Cayenne.objectForQuery(cayenneService.sharedContext(), q);
	}
}
