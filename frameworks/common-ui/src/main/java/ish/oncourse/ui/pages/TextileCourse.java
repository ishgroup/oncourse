package ish.oncourse.ui.pages;

import java.util.Calendar;
import java.util.List;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.textile.TextileUtil;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

/**
 * The auxiliary page for displaying {course} textile
 * 
 * @author ksenia
 * 
 */
public class TextileCourse {
	private static final Expression CURRENT_CLASS_QUALIFIER = ExpressionFactory.greaterExp(
			CourseClass.END_DATE_PROPERTY, Calendar.getInstance().getTime());

	@Inject
	private Request request;

	@Property
	private Course course;

	@Property
	private boolean showClasses;

	@Property
	private CourseClass courseClass;

	void beginRender() {
		course = (Course) request.getAttribute(TextileUtil.TEXTILE_COURSE_PAGE_PARAM);
		showClasses = Boolean.valueOf((String) request
				.getAttribute(TextileUtil.TEXTILE_COURSE_SHOW_CLASSES_PARAM))
			&& !getClassesToShow().isEmpty();
	}

	public List<CourseClass> getClassesToShow() {
		return CURRENT_CLASS_QUALIFIER.filterObjects(course.getEnrollableClasses());
	}
}
