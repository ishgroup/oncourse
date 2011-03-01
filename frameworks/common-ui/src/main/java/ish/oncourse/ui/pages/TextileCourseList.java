package ish.oncourse.ui.pages;

import java.util.List;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.textile.TextileUtil;

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

	void beginRender() {
		courses = (List<CourseClass>) request
				.getAttribute(TextileUtil.TEXTILE_COURSE_LIST_PAGE_PARAM);
	}
}
