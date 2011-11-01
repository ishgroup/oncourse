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
	
	Object onActivate(Object id) {
		try {
			long idLong = Long.parseLong((String) id);
			List<CourseClass> list = courseClassService.loadByIds(idLong);
			this.courseClass = (!list.isEmpty()) ? list.get(0) : null;
		} catch (Exception e) {
			return pageNotFound;
		}
		return null;
	}
}
