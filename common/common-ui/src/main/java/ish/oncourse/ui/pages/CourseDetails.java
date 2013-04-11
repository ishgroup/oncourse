package ish.oncourse.ui.pages;

import ish.oncourse.model.Course;
import ish.oncourse.services.html.IFacebookMetaProvider;
import ish.oncourse.ui.utils.CourseItemModel;
import ish.oncourse.util.HTMLUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class CourseDetails {
	@Inject
	private Request request;

	@Inject
	private IFacebookMetaProvider facebookMetaProvider;

	@Property
	private Course course;

	void beginRender() {
		course = (Course) request.getAttribute(Course.class.getSimpleName());
	}

	public CourseItemModel getCourseItemModel() {
		return CourseItemModel.createCourseItemModel(course, null);
	}

	public String getCanonicalLinkPath() {
		return HTMLUtils.getCanonicalLinkPathFor(course, request);
	}

	public String getCourseDetailsTitle() {
		if (course == null) {
			return "Course Not Found";
		}
		return course.getName();
	}

	public String getMetaDescription() {
		return facebookMetaProvider.getDescriptionContent(course);
	}
}
