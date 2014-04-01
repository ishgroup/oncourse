package ish.oncourse.portal.components;

import ish.oncourse.model.CourseClass;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class Messages {

	@Parameter
	@Property
	private CourseClass courseClass;

	@SetupRender
	boolean setupRender() {
        return courseClass != null;
    }
	
}
