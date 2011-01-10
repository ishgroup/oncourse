package ish.oncourse.ui.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.site.IWebSiteService;

import java.util.Arrays;
import java.util.Collection;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class CourseClassShortlistControl {
	@Parameter
	@Property
	private CourseClass courseClass;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICookiesService cookiesService;

	private Collection<String> shortListedClassesIds;

	@SetupRender
	void beginRender() {
		String[] idsArray = cookiesService
				.getCookieCollectionValue(CourseClass.SHORTLIST_COOKEY_KEY);
		if (idsArray != null) {
			shortListedClassesIds = Arrays.asList(idsArray);
		}
	}

	public boolean isPaymentGatewayEnabled() {
		return webSiteService.getCurrentCollege().getIsWebSitePaymentsEnabled();
	}

	public boolean isContainedInShortList() {

		if (shortListedClassesIds == null) {
			return false;
		}
		return shortListedClassesIds.contains(courseClass.getId().toString());
	}
}
