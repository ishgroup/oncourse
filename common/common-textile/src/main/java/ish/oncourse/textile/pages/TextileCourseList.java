package ish.oncourse.textile.pages;

import java.util.List;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.textile.TextileUtil;

import ish.oncourse.services.textile.attrs.CourseStyle;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class TextileCourseList {

	@Inject
	private Request request;

	@Property
	private List<CourseClass> courses;

	@Property
	private Course course;

	@Property
	private CourseStyle style;

	void beginRender() {
		courses = (List<CourseClass>) request
				.getAttribute(TextileUtil.TEXTILE_COURSE_LIST_PAGE_PARAM);

		style = (CourseStyle) request
				.getAttribute(TextileUtil.TEXTILE_COURSE_STYLE_PARAM);
		if (style == null)
			style = CourseStyle.details;

	}

	public boolean isTitles()
	{
		return style == CourseStyle.titles;
	}

	public boolean isDetails()
	{
		return style == CourseStyle.details;
	}

}
