package ish.oncourse.website.components;

import ish.oncourse.model.Course;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class CourseItem {
	@Property
	@Parameter(required = true)
	private Course course;
	
	@Property
	private CourseClassItem courseClass;
}
