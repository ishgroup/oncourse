package ish.oncourse.ui.pages;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import ish.oncourse.model.Course;
import ish.oncourse.services.course.ICourseService;

public class CourseDetails {
	@Inject
	private Request request;

	@Property
	private Course course;

	@Inject
	private ICourseService courseService;

	void beginRender() {
		String id = request.getParameter("id");
		String code = (String) request.getAttribute("courseCode");
		if (id != null) {
			List<Course> courses = courseService.loadByIds(id);
			course = (courses.size() > 0) ? courses.get(0) : null;
		} else if(code!=null){
			course = courseService.getCourse(Course.CODE_PROPERTY, code.toUpperCase());
		}
	}
}
