package ish.oncourse.ui.pages;

import ish.oncourse.model.Course;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
/**
 * The auxiliary page for displaying {course} textile
 * @author ksenia
 *
 */
public class CoursePage {
	@Inject
	private Request request;

	@Property
	private Course course;

	void beginRender() {
		course = (Course) request.getAttribute("course");
	}
}
