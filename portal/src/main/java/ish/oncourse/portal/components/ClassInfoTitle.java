package ish.oncourse.portal.components;

import ish.oncourse.model.CourseClass;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class ClassInfoTitle {
	@Parameter(required = true)
	@Property
	private CourseClass courseClass;

	@Parameter(required = true)
	@Property
	private String selected;
}
