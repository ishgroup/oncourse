package ish.oncourse.ui.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.ICourseClassService;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Based on DynamicCookieView
 * 
 * @author ksenia
 * 
 */
public class ShortList {

	@Inject
	private ICookiesService cookiesService;
	@Inject
	private ICourseClassService courseClassService;
	@Property
	private List<CourseClass> ordered;

	@SetupRender
	void beforeRender() {
		String[] orderedClassesIds = cookiesService
				.getCookieCollectionValue(CourseClass.SHORTLIST_COOKEY_KEY);
		if (orderedClassesIds != null) {
			ordered = courseClassService.loadByIds(orderedClassesIds);
		}
	}

	public Integer getOrderedCount() {
		if (ordered == null) {
			return 0;
		}
		return ordered.size();
	}

	public String getSelectedMessage() {
		return "course" + (ordered == null || ordered.size() != 1 ? "s" : "") + " selected";
	}

	public boolean isHasObjects() {
		return ordered != null && !ordered.isEmpty();
	}
}
