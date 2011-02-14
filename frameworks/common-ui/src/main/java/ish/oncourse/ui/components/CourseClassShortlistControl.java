package ish.oncourse.ui.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.services.cookies.ICookiesService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
@Deprecated
public class CourseClassShortlistControl {
	@Parameter
	@Property
	private CourseClass courseClass;

	@Inject
	private ICookiesService cookiesService;

	private Collection<Long> shortListedClassesIds;

	@SetupRender
	void beginRender() {
		shortListedClassesIds  = cookiesService.getCookieCollectionValue(
				CourseClass.SHORTLIST_COOKIE_KEY, Long.class);
		
	}

	public boolean isContainedInShortList() {

		if (shortListedClassesIds == null) {
			return false;
		}
		return shortListedClassesIds.contains(courseClass.getId());
	}
}
