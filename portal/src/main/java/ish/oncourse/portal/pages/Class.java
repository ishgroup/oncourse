package ish.oncourse.portal.pages;

import ish.oncourse.model.CourseClass;
import ish.oncourse.services.courseclass.ICourseClassService;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Class {

	@Property
	private CourseClass courseClass;

	@Inject
	private ICourseClassService courseClassService;
	
	void onActivate(long id) {
		List<CourseClass> list = courseClassService.loadByIds(id);
		this.courseClass = (!list.isEmpty()) ? list.get(0) : null;
	}
}
