package ish.oncourse.ui.pages;

import ish.oncourse.model.Course;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class CourseDetails {
	@Inject
	private Request request;

	@Property
	private Course course;

	void beginRender() {
		course = (Course) request.getAttribute(Course.class.getSimpleName());
	}
	
	public String getCourseDetailsTitle(){
		if(course==null){
			return "Course Not Found";
		}
		return course.getName();
	}
}
