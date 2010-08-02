package ish.oncourse.website.pages;

import java.util.List;

import ish.oncourse.model.Course;
import ish.oncourse.services.course.ICourseService;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class CourseList  {
	
	@Inject
	private ICourseService courseService;
	
	@Property
	private List<Course> courses;
	
	@Property
	private Course course;
	
	@SetupRender
	public void beforeRender() {
		this.courses = courseService.getCourses();
	}
}
