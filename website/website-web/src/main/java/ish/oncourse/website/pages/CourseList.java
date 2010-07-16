package ish.oncourse.website.pages;

import ish.oncourse.model.Course;
import ish.oncourse.services.course.ICourseService;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class CourseList  {
	
	@Property
	@Inject
	private ICourseService courseService;
	
	@Property
	private Course course;
}
