package ish.oncourse.services.courseclass;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

public class CourseClassService implements ICourseClassService {
	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	@SuppressWarnings("unchecked")
	public CourseClass getCourseClassByFullCode(String code) {
		String[] parts = code.split("-");
		String courseCode = parts[0];
		String courseClassCode = parts[1];
		SelectQuery query = new SelectQuery(CourseClass.class,
				getSiteQualifier().andExp(
						getSearchStringPropertyQualifier(
								CourseClass.COURSE_PROPERTY + "."
										+ Course.CODE_PROPERTY, courseCode))
						.andExp(
								getSearchStringPropertyQualifier(
										CourseClass.CODE_PROPERTY,
										courseClassCode)));
		List<CourseClass> result = cayenneService.sharedContext().performQuery(
				query);
		return !result.isEmpty() ? result.get(0) : null;
	}

	/**
	 * @return
	 */
	private Expression getSiteQualifier() {
		return ExpressionFactory.matchExp(Course.COLLEGE_PROPERTY,
				webSiteService.getCurrentCollege());
	}

	public Expression getSearchStringPropertyQualifier(String searchProperty,
			Object value) {
		return ExpressionFactory.likeIgnoreCaseExp(searchProperty, value);
	}

	public List<CourseClass> loadByIds(Object... ids) {
		if (ids.length == 0) {
			return Collections.emptyList();
		}

		List<Object> params = Arrays.asList(ids);

		SelectQuery q = new SelectQuery(CourseClass.class, ExpressionFactory.inDbExp(CourseClass.ID_PK_COLUMN, params));

		return cayenneService.sharedContext().performQuery(q);
	}
}
