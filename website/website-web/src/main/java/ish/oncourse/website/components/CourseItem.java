package ish.oncourse.website.components;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

public class CourseItem {

	@Inject
	private Messages messages;

	@Property
	@Parameter(required = true)
	private Course course;

	@Property
	private CourseClass courseClass;

	public String getAvailMsg() {
		int numberOfClasses = course.getEnrollableClasses().size();
		String msg;

		if (numberOfClasses <= 0) {
			msg = messages.get("noClasses");
		}else if (numberOfClasses == 1) {
			msg = messages.get("oneClass");
		} else {
			msg = messages.format("someClasses", numberOfClasses);
		}

		return msg;
	}

	public String getMoreLink() {
		return "/course/" + course.getCode();
	}

}
