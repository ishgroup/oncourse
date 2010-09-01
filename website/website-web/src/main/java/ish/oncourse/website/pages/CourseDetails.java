package ish.oncourse.website.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import ish.oncourse.model.Course;

public class CourseDetails {
	@Inject
	private Request request;
	
	@Property
	private Course course;
	
	void beginRender(){
		course=(Course) request.getAttribute("course");
	}

}
