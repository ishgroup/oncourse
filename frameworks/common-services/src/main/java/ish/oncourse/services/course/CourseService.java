package ish.oncourse.services.course;

import ish.oncourse.model.Course;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class CourseService implements ICourseService {

	@Inject
	private Request request;
	
	@Inject
	private ICayenneService cayenneService;
	
	@Inject
    private IWebSiteService webSiteService;
	
	public List<Course> getCourses() {
		SelectQuery q = new SelectQuery(Course.class);
		q.andQualifier(ExpressionFactory.matchExp(Course.COLLEGE_PROPERTY, webSiteService.getCurrentCollege()));
		q.setFetchLimit(30);
		return cayenneService.sharedContext().performQuery(q);
	}
}
