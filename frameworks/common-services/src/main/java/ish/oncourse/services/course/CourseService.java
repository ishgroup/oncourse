package ish.oncourse.services.course;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.College;
import ish.oncourse.model.Course;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
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

	public List<Course> loadByIds(Object... ids) {

		if (ids.length == 0) {
			return Collections.emptyList();
		}

		List<Object> params = Arrays.asList(ids);

		EJBQLQuery q = new EJBQLQuery(
				"select c from Course c where c.id IN (:ids)");

		q.setParameter("ids", params);

		return cayenneService.sharedContext().performQuery(q);
	}

	@SuppressWarnings("unchecked")
	public Course getCourse(String searchProperty, Object value) {
		College currentCollege = webSiteService.getCurrentCollege();
		ObjectContext sharedContext = cayenneService.sharedContext();
		Expression qualifier = ExpressionFactory.matchExp(
				BinaryInfo.COLLEGE_PROPERTY, currentCollege);
		if (searchProperty != null) {
			qualifier = qualifier.andExp(ExpressionFactory.matchExp(
					searchProperty, value));
		}
		SelectQuery q = new SelectQuery(Course.class, qualifier);
		List<Course> result = sharedContext.performQuery(q);
		return !result.isEmpty() ? result.get(0) : null;
	}

	public List<Course> getCourses(boolean enrollable) {
		List<Course> result = new ArrayList<Course>();
		List<Course> courses = getCourses();
		if (enrollable) {
			for (Course course : courses) {
				if (!course.getEnrollableClasses().isEmpty()) {
					result.add(course);
				}
			}
		} else {
			for (Course course : courses) {
				if (course.getEnrollableClasses().isEmpty()) {
					result.add(course);
				}
			}
		}
		return result;
	}
}
