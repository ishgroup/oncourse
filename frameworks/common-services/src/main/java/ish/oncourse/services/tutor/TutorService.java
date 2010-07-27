package ish.oncourse.services.tutor;

import ish.oncourse.model.Tutor;
import ish.oncourse.services.persistence.ICayenneService;

import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

public class TutorService implements ITutorService{
	@Inject
	private ICayenneService cayenneService;
	
	public Tutor getTutorById(Long tutorId) {
		SelectQuery query = new SelectQuery(Tutor.class, ExpressionFactory.matchExp(Tutor.ID_PK_COLUMN, tutorId));
		List<Tutor> result = cayenneService.sharedContext().performQuery(query);
		return result!=null&&!result.isEmpty()?result.get(0):null;
	}
	
}
