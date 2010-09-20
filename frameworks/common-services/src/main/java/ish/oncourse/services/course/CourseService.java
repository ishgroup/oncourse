package ish.oncourse.services.course;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.College;
import ish.oncourse.model.Course;
import ish.oncourse.model.Site;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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

	public List<Course> getCourses(Integer startDefault, Integer rowsDefault) {
		SelectQuery q = new SelectQuery(Course.class);
		q.andQualifier(getSiteQualifier());
		if (startDefault == null) {
			startDefault = START_DEFAULT;
		}
		if (rowsDefault == null) {
			rowsDefault = ROWS_DEFAULT;
		}
		q.setFetchOffset(startDefault);
		q.setFetchLimit(rowsDefault);
		return cayenneService.sharedContext().performQuery(q);
	}

	/**
	 * @return
	 */
	private Expression getSiteQualifier() {
		return ExpressionFactory.matchExp(Course.COLLEGE_PROPERTY,
				webSiteService.getCurrentCollege());
	}
	/**
	 * @return
	 */
	private Expression getAvailabilityQualifier() {
		return ExpressionFactory.matchExp(Course.IS_WEB_VISIBLE_PROPERTY, true);
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
			if (searchProperty.equals(Course.CODE_PROPERTY)) {
				qualifier = qualifier.andExp(getSearchStringPropertyQualifier(
						searchProperty, value));
			} else {
				qualifier = qualifier.andExp(ExpressionFactory.matchExp(
						searchProperty, value));
			}
		}
		SelectQuery q = new SelectQuery(Course.class, qualifier);
		List<Course> result = sharedContext.performQuery(q);
		return !result.isEmpty() ? result.get(0) : null;
	}

	public Expression getSearchStringPropertyQualifier(String searchProperty,
			Object value) {
		return ExpressionFactory.likeIgnoreCaseExp(searchProperty, value);
	}

	public List<Course> getCourses(boolean enrollable) {
		List<Course> result = new ArrayList<Course>();
		List<Course> courses = getCourses(null, null);
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

	public Integer getCoursesCount() {
		return ((Number) cayenneService.sharedContext().performQuery(
				new EJBQLQuery("select count(c) from Course c where "
						+ getSiteQualifier().toEJBQL("c"))).get(0)).intValue();
	}

	public Date getLatestModifiedDate() {
		return (Date) cayenneService.sharedContext().performQuery(
				new EJBQLQuery("select max(c.modified) from Course c where "
						+ getSiteQualifier().andExp(getAvailabilityQualifier()).toEJBQL("c"))).get(0);
	}
}
