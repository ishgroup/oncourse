package ish.oncourse.services.course;

import ish.oncourse.model.Course;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.Collections;
import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

public class CourseService implements ICourseService {

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	public List<Course> getCourses() {
		SelectQuery q = new SelectQuery(Course.class);
		q.andQualifier(ExpressionFactory.matchExp(Course.COLLEGE_PROPERTY,
				webSiteService.getCurrentCollege()));
		q.setFetchLimit(30);
		return cayenneService.sharedContext().performQuery(q);
	}

	public List<Course> loadByIds(List<String> ids) {
		
		if (ids.isEmpty()) {
			return Collections.emptyList();
		}
		
		EJBQLQuery q = new EJBQLQuery(
				"select c from Course c where c.id IN (:ids)");

		q.setParameter("ids", ids);

		return cayenneService.sharedContext().performQuery(q);
	}
}
