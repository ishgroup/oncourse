package ish.oncourse.portal.pages;

import ish.oncourse.model.CourseClass;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class Class {

	@SuppressWarnings("all")
	@Property
	private CourseClass courseClass;
	
	@Inject
	private ICourseClassService courseClassService;

	@Inject
	private ICayenneService cayenneService;
	
	@InjectPage
	private PageNotFound pageNotFound;
	
	Object onActivate(String id) {
		if (id != null && id.length() > 0 && id.matches("\\d+")) {
			long idLong = Long.parseLong(id);
			/**
			 * We need to use not shared cayenne context to be sure that we get actual data
			 * for all related objects of the course class (sessions, room, sites).
			 * It is important when we define timezone for start and end time.
			 */
			List<CourseClass> list = courseClassService.loadByIds(cayenneService.newNonReplicatingContext(), idLong);
			this.courseClass = (!list.isEmpty()) ? list.get(0) : null;
		} else {
			return pageNotFound;
		}
		return null;
	}
}
