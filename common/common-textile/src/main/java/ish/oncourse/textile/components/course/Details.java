package ish.oncourse.textile.components.course;

import ish.oncourse.model.Course;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class Details {
	@Parameter
	@Property
	private Course course;

	@BeginRender
	void beginRender() {
		System.out.println(course);
	}
}
