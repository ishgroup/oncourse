package ish.oncourse.portal.pages;

import ish.oncourse.model.CourseClass;
import ish.oncourse.services.courseclass.ICourseClassService;

import java.util.List;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Class {

	@Property
	private CourseClass courseClass;
	
	@Inject
	private ICourseClassService courseClassService;
	
	@InjectPage
	private PageNotFound pageNotFound;
	
	Object onActivate(String id) {
		if (id != null && id.length() > 0 && id.matches("\\d+")) {
			long idLong = Long.parseLong(id);
			List<CourseClass> list = courseClassService.loadByIds(idLong);
			this.courseClass = (!list.isEmpty()) ? list.get(0) : null;
		} else {
			return pageNotFound;
		}
		return null;
	}
}
